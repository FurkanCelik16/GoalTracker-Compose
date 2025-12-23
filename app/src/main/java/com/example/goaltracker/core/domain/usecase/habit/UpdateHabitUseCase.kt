package com.example.goaltracker.core.domain.usecase.habit

import com.example.goaltracker.core.domain.repository.HabitRepository
import com.example.goaltracker.core.model.Habit
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(private val repository: HabitRepository) {
    suspend operator fun invoke(habit: Habit){
        repository.updateHabit(habit)
    }
}