package com.example.goaltracker.core.domain.usecase.analysis

import com.example.goaltracker.core.domain.repository.HabitRepository
import com.example.goaltracker.core.model.HabitType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.max

class GetTotalCompletedCountUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<Int> {
        return combine(
            habitRepository.allHabits,
            habitRepository.getAllEntries()
        ) { habits, entries ->
            var totalSuccess = 0
            val today = LocalDate.now()

            habits.forEach { habit ->
                val habitEntriesCount = entries.count { it.habitId == habit.id }

                if (habit.type == HabitType.POSITIVE) {
                    totalSuccess += habitEntriesCount
                } else {
                    val daysElapsed = ChronoUnit.DAYS.between(habit.startDate, today) + 1

                    val possibleSuccesses = max(0, daysElapsed.toInt())

                    val actualSuccesses = max(0, possibleSuccesses - habitEntriesCount)

                    totalSuccess += actualSuccesses
                }
            }
            totalSuccess
        }
    }
}