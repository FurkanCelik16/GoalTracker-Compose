package com.example.goaltracker.core.domain.usecase.habit

import com.example.goaltracker.core.domain.repository.HabitRepository
import com.example.goaltracker.core.model.Habit
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(private val repository: HabitRepository){
    suspend operator fun invoke(habit: Habit){
        if(habit.name.isBlank()) return
        repository.insertHabit(habit)
    }
}