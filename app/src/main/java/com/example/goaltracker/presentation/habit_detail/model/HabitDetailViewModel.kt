package com.example.goaltracker.presentation.habit_detail.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goaltracker.core.domain.usecase.habit.DeleteHabitUseCase
import com.example.goaltracker.core.domain.usecase.habit.GetHabitHistoryUseCase
import com.example.goaltracker.core.domain.usecase.habit.GetHabitsUseCase
import com.example.goaltracker.core.domain.usecase.habit.UpdateHabitUseCase
import com.example.goaltracker.core.model.Habit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    getHabitsUseCase: GetHabitsUseCase,
    getHabitHistoryUseCase: GetHabitHistoryUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: Int = checkNotNull(savedStateHandle["habitId"])

    val habit = getHabitsUseCase()
        .map { list -> list.find { it.id == habitId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val history = getHabitHistoryUseCase(habitId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            updateHabitUseCase(habit)
        }
    }

    fun deleteHabit(habit: Habit, onComplete: () -> Unit) {
        viewModelScope.launch {
            deleteHabitUseCase(habit)
            onComplete()
        }
    }
}