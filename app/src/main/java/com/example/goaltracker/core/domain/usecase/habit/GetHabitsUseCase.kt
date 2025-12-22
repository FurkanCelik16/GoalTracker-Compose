package com.example.goaltracker.core.domain.usecase.habit

import com.example.goaltracker.core.data.repository.HabitRepository
import com.example.goaltracker.core.model.Habit
import com.example.goaltracker.core.model.HabitFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    operator fun invoke(filter: HabitFilter = HabitFilter.ALL, date: LocalDate = LocalDate.now()): Flow<List<Habit>> {
        return combine(
            repository.allHabits,
            repository.getCompletedHabitIds(date)
        ) { habits, completedIds ->
            when (filter) {
                HabitFilter.ALL -> habits
                HabitFilter.TODAY_PENDING -> habits.filter { it.id !in completedIds }
                HabitFilter.COMPLETED -> habits.filter { it.id in completedIds }
            }
        }
    }
}