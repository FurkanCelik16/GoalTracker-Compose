package com.example.goaltracker.presentation.goal_detail.model

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goaltracker.core.domain.usecase.challenge.CheckChallengeProgressUseCase
import com.example.goaltracker.core.domain.usecase.challenge.DeleteChallengeUseCase
import com.example.goaltracker.core.domain.usecase.goal.GetGoalDetailUseCase
import com.example.goaltracker.core.domain.usecase.goal.GetGoalHistoryUseCase
import com.example.goaltracker.core.domain.usecase.goal.UpdateGoalUseCase
import com.example.goaltracker.core.domain.usecase.goal.DeleteGoalUseCase
import com.example.goaltracker.core.domain.usecase.goal.UpdateGoalProgressUseCase
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import com.example.goaltracker.core.model.GoalType
import com.example.goaltracker.core.model.ReminderType
import com.example.goaltracker.core.worker.GoalReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    getGoalDetailUseCase: GetGoalDetailUseCase,
    getGoalHistoryUseCase: GetGoalHistoryUseCase,
    private val updateGoalProgressUseCase: UpdateGoalProgressUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase,
    private val deleteChallengeUseCase: DeleteChallengeUseCase,
    private val checkChallengeProgressUseCase: CheckChallengeProgressUseCase,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val goalId: Int = checkNotNull(savedStateHandle["goalId"])

    val goal = getGoalDetailUseCase(goalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val history = getGoalHistoryUseCase(goalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _chartTimeRange = MutableStateFlow(ChartTimeRange.WEEK)
    val chartTimeRange = _chartTimeRange.asStateFlow()

    private val _chartSelectedDate = MutableStateFlow(LocalDate.now())
    val chartSelectedDate = _chartSelectedDate.asStateFlow()

    val chartData = combine(history, _chartTimeRange, _chartSelectedDate, goal) { historyList, range, date, currentGoal ->
        calculateChartData(historyList, range, date, currentGoal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateChartRange(range: ChartTimeRange) { _chartTimeRange.value = range }
    fun updateChartDate(date: LocalDate) { _chartSelectedDate.value = date }



    fun updateProgress(newCurrentAmount: Float) {
        val currentGoal = goal.value ?: return
        viewModelScope.launch {
            updateGoalUseCase(currentGoal.copy(currentAmount = newCurrentAmount))
            val parentTitle = currentGoal.parentChallengeTitle
            if (parentTitle != null) {
                checkChallengeProgressUseCase(parentTitle, LocalDate.now())
            }
        }
    }
    fun updateGoal(updatedGoal: Goal) {
        viewModelScope.launch {
            updateGoalUseCase(updatedGoal)

            if (updatedGoal.parentChallengeTitle != null) {
                checkChallengeProgressUseCase(updatedGoal.parentChallengeTitle, LocalDate.now())
            }
        }
    }

    fun deleteGoal(onDeleteComplete: () -> Unit) {
        val currentGoal = goal.value ?: return

        viewModelScope.launch {
            val challengeTitleToDelete = when {
                currentGoal.isChallengeMaster -> currentGoal.title
                currentGoal.parentChallengeTitle != null -> currentGoal.parentChallengeTitle
                else -> null
            }

            if (challengeTitleToDelete != null) {
                deleteChallengeUseCase(challengeTitleToDelete)
            } else {
                deleteGoalUseCase(currentGoal)
            }

            onDeleteComplete()
        }
    }
    fun addProgress(goal: Goal, amount: Float, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            updateGoalProgressUseCase(goal, amount, date)
            val parentTitle = goal.parentChallengeTitle
            if (parentTitle != null) {
                checkChallengeProgressUseCase(parentTitle, date)
            }
        }
    }
    private fun calculateChartData(
        history: List<GoalHistoryEntity>,
        rangeType: ChartTimeRange,
        selectedDate: LocalDate,
        goal: Goal?
    ): List<GoalHistoryEntity> {
        if (goal == null) return emptyList()

        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now()
        val isAccumulative = goal.type == GoalType.ACCUMULATIVE
        val (startDate, endDate) = if (rangeType == ChartTimeRange.WEEK) {
            selectedDate.minusDays(6) to selectedDate
        } else {
            val yearMonth = java.time.YearMonth.from(selectedDate)
            yearMonth.atDay(1) to yearMonth.atEndOfMonth()
        }

        val daysBetween = ChronoUnit.DAYS.between(startDate, endDate).toInt()
        val fullDataList = mutableListOf<GoalHistoryEntity>()
        val startMillis = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli()

        var runningTotal = if (isAccumulative) {
            history.filter { it.date < startMillis }
                .map { it.value }
                .sum()
        } else {
            0f
        }

        for (i in 0..daysBetween) {
            val dateCheck = startDate.plusDays(i.toLong())
            val dateMillis = dateCheck.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val isFuture = dateCheck.isAfter(today)
            val dailyIncrement = history.filter {
                val hDate = Instant.ofEpochMilli(it.date).atZone(zoneId).toLocalDate()
                hDate.isEqual(dateCheck)
            }.sumOf { it.value.toDouble() }.toFloat()

            val chartValue = if (isAccumulative) {
                runningTotal += dailyIncrement
                runningTotal
            } else {
                dailyIncrement
            }

            val finalValue = if (isFuture && dailyIncrement == 0f) {
                if (isAccumulative) runningTotal else 0f
            } else {
                chartValue
            }

            fullDataList.add(
                GoalHistoryEntity(
                    id = i,
                    goalId = goal.id,
                    value = finalValue,
                    date = dateMillis
                )
            )
        }
        return fullDataList
    }
    fun setReminder(goal: Goal, type: ReminderType, start: String, end: String?, interval: Int) {
        viewModelScope.launch {
            val updatedGoal = goal.copy(
                reminderType = type,
                reminderStartTime = start,
                reminderEndTime = end,
                reminderIntervalHours = interval
            )
            updateGoalUseCase(updatedGoal)

            if (type != ReminderType.NONE) {
                GoalReminderWorker.scheduleNextReminder(
                    context = context,
                    goalId = goal.id,
                    type = type,
                    startTimeStr = start,
                    endTimeStr = end,
                    intervalHours = interval
                )
            } else {
                GoalReminderWorker.cancelReminder(context, goal.id)
            }
        }
    }
    fun getProgressForDate(date: LocalDate): Float {

        val historyItem = history.value.find {
            val hDate = Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
            hDate == date
        }

        return historyItem?.value ?: 0f
    }


}
