package com.example.goaltracker.presentation.home.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goaltracker.core.data.repository.HabitRepository
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
                    _streakAnimationTrigger.value = updatedHabit?.streak
                } else if (habit.type == HabitType.NEGATIVE && isNowCompleted) {
                    _streakFailureTrigger.value = true
                }
            }
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
    fun getHabitFlow(habitId: Int): Flow<Habit?> {
        return habits.map { list -> list.find { it.id == habitId } }
    }
    fun getHabitHistory(habitId: Int): Flow<List<HabitEntry>> {
        return habitRepository.getHistory(habitId)
    }
}