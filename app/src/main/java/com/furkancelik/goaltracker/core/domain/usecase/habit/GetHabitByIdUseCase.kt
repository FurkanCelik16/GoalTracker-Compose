package com.furkancelik.goaltracker.core.domain.usecase.habit

import com.furkancelik.goaltracker.core.domain.repository.HabitRepository
import com.furkancelik.goaltracker.core.model.Habit
import javax.inject.Inject

class GetHabitByIdUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(id: Int): Habit? {
        return repository.getHabitById(id)
    }
}