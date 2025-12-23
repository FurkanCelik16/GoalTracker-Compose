package com.example.goaltracker.core.domain.usecase.analysis

import com.example.goaltracker.core.domain.repository.AnalysisRepository
import com.example.goaltracker.core.domain.repository.HabitRepository
import com.example.goaltracker.core.model.HabitDifficulty
import com.example.goaltracker.core.model.HabitType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

class CalculateWeeklyStatsUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val analysisRepository: AnalysisRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(selectedDateFlow: Flow<LocalDate>): Flow<List<Pair<String, Int>>> {
        return combine(
            selectedDateFlow,
            habitRepository.allHabits,
            selectedDateFlow.flatMapLatest { date ->
                analysisRepository.getEntriesForDateRange(date.minusDays(6), date)
            }
        ) { endDate, habits, entries ->
            val startDate = endDate.minusDays(6)
            val stats = mutableListOf<Pair<String, Int>>()

            for (i in 0..6) {
                val dateToCheck = startDate.plusDays(i.toLong())
                val completedIdsAtDate = entries
                    .filter { it.date == dateToCheck }
                    .map { it.habitId }

                val dailyScore = habits.filter { habit ->
                    habit.id in completedIdsAtDate && habit.type == HabitType.POSITIVE
                }.sumOf { habit ->
                    val points:Int = when (habit.difficulty) {
                        HabitDifficulty.HARD -> 20
                        HabitDifficulty.MEDIUM -> 10
                        HabitDifficulty.EASY -> 5
                    }
                    points
                }

                val dayName = dateToCheck.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("tr"))
                stats.add(dayName to dailyScore)
            }
            stats
        }
    }
}