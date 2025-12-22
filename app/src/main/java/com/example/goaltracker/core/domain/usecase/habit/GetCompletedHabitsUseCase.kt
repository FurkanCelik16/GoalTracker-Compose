package com.example.goaltracker.core.domain.usecase.habit

import com.example.goaltracker.core.data.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetCompletedHabitIdsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Int>> {
        return repository.getCompletedHabitIds(date)
    }
}