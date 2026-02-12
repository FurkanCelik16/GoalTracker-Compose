package com.example.goaltracker.presentation.habit_detail.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goaltracker.core.domain.usecase.challenge.DeleteChallengeUseCase
import com.example.goaltracker.core.domain.usecase.habit.DeleteHabitUseCase
import com.example.goaltracker.core.domain.usecase.habit.GetHabitHistoryUseCase
import com.example.goaltracker.core.domain.usecase.habit.GetHabitsUseCase
import com.example.goaltracker.core.domain.usecase.habit.UpdateHabitUseCase
import com.example.goaltracker.core.model.Habit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    getHabitsUseCase: GetHabitsUseCase,
    getHabitHistoryUseCase: GetHabitHistoryUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val deleteChallengeUseCase: DeleteChallengeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: Int = checkNotNull(savedStateHandle["habitId"])

    val habit = getHabitsUseCase()
        .map { list -> list.find { it.id == habitId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val history = getHabitHistoryUseCase(habitId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    fun previousMonth() {
        _selectedDate.update { it.minusMonths(1) }
    }

    fun nextMonth() {
        _selectedDate.update { it.plusMonths(1) }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            updateHabitUseCase(habit)
        }
    }

    fun deleteHabit(habit: Habit, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (habit.isChallenge) {
                deleteChallengeUseCase(habit.category)
            } else {
                deleteHabitUseCase(habit)
            }
            onComplete()
        }
    }
}