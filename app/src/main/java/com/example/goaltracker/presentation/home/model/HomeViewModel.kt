package com.example.goaltracker.presentation.home.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goaltracker.core.domain.repository.HabitRepository
import com.example.goaltracker.core.domain.usecase.challenge.CheckChallengeProgressUseCase
import com.example.goaltracker.core.domain.usecase.challenge.DeleteChallengeUseCase
import com.example.goaltracker.core.domain.usecase.goal.CheckAndResetRecurringGoalsUseCase
import com.example.goaltracker.core.domain.usecase.habit.*
import com.example.goaltracker.core.domain.usecase.logic.RefreshStreaksUseCase
import com.example.goaltracker.core.domain.usecase.logic.ToggleHabitUseCase
import com.example.goaltracker.core.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val addHabitUseCase: AddHabitUseCase,
    private val toggleHabitUseCase: ToggleHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val refreshStreaksUseCase: RefreshStreaksUseCase,
    private val getHabitByIdUseCase: GetHabitByIdUseCase,
    private val habitRepository: HabitRepository,
    private val deleteChallengeUseCase: DeleteChallengeUseCase,
    private val checkAndResetRecurringGoalsUseCase: CheckAndResetRecurringGoalsUseCase,
    private val checkChallengeProgressUseCase: CheckChallengeProgressUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _streakAnimationTrigger = MutableStateFlow<Int?>(null)
    val streakAnimationTrigger = _streakAnimationTrigger.asStateFlow()

    private val _streakFailureTrigger = MutableStateFlow(false)
    val streakFailureTrigger = _streakFailureTrigger.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val habits = _selectedDate.flatMapLatest { date ->
        getHabitsUseCase(HabitFilter.ALL, date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyEntries = _selectedDate.flatMapLatest { date ->
        habitRepository.getEntriesByDate(date)
            .map { list -> list.associateBy { it.habitId } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    val completedHabitIds = combine(habits, dailyEntries) { habitList, entryMap ->
        entryMap.filter { (id, entry) ->
            val target = habitList.find { it.id == id }?.targetCount ?: 1
            entry.amount >= target
        }.keys.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            refreshStreaksUseCase()
            checkAndResetRecurringGoalsUseCase()
        }
    }

    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            val currentDate = _selectedDate.value
            if (currentDate.isAfter(LocalDate.now())) return@launch

            val entryBefore = dailyEntries.value[habit.id]
            val wasCompleted = (entryBefore?.amount ?: 0f) >= habit.targetCount

            toggleHabitUseCase(habit, currentDate)

            if (habit.isChallenge) {
                checkChallengeProgressUseCase(habit.category, currentDate)
            }
            if (currentDate == LocalDate.now()) {

                val updatedEntry = habitRepository.getEntryForDate(habit.id, currentDate)
                val isNowCompleted = (updatedEntry?.amount ?: 0f) >= habit.targetCount

                if (habit.type == HabitType.POSITIVE && !wasCompleted && isNowCompleted) {
                    val updatedHabit = getHabitByIdUseCase(habit.id)
                    val currentStreak = updatedHabit?.streak ?: 0
                    if(shouldStreakShowAnimation(currentStreak)){
                        _streakAnimationTrigger.value = currentStreak
                    }
                } else if (habit.type == HabitType.NEGATIVE && isNowCompleted) {
                    _streakFailureTrigger.value = true
                }
            }
        }
    }
    private fun shouldStreakShowAnimation(streak:Int):Boolean{
        val milestones = setOf(1,3,7,14,21,30,90,365)

        return when{
            streak in milestones -> true
            streak in 31..<90 -> (streak-30) % 7 == 0
            streak in 91..<365 -> (streak-90) % 30 == 0
            streak > 365 -> streak % 365 == 0
            else -> false
        }
    }

    fun selectDate(date: LocalDate) { _selectedDate.value = date }
    fun resetStreakAnimation() { _streakAnimationTrigger.value = null }
    fun resetFailureStreakAnimation() { _streakFailureTrigger.value = false }

    fun addHabit(name: String, category: String, period: Period, difficulty: HabitDifficulty,
                 type: HabitType, timeOfDay: TimeOfDay, selectedDays: List<Int>, interval: Int, targetCount: Int) {
        viewModelScope.launch {
            val newHabit = Habit(name = name, category = category, period = period, difficulty = difficulty, type = type, timeOfDay = timeOfDay, startDate = LocalDate.now(), selectedDays = selectedDays, periodInterval = interval, targetCount = targetCount)
            addHabitUseCase(newHabit)
        }
    }

    fun updateHabit(habit: Habit) = viewModelScope.launch { updateHabitUseCase(habit) }

    fun deleteHabit(habit: Habit) = viewModelScope.launch {
        if (habit.isChallenge) deleteChallengeUseCase(habit.category) else deleteHabitUseCase(habit)
    }
}