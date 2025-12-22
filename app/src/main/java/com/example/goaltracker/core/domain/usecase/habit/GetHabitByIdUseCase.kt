package com.example.goaltracker.core.domain.usecase.habit

import com.example.goaltracker.core.data.repository.HabitRepository
import com.example.goaltracker.core.model.Habit
import javax.inject.Inject

class GetHabitByIdUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(id: Int): Habit? {
        return repository.getHabitById(id)
    }
}