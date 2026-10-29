package com.furkancelik.goaltracker.core.domain.usecase.challenge

import com.furkancelik.goaltracker.core.domain.repository.GoalRepository
import com.furkancelik.goaltracker.core.domain.repository.HabitRepository
import com.furkancelik.goaltracker.core.model.Goal
import com.furkancelik.goaltracker.core.model.GoalType
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class CheckChallengeProgressUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val habitRepository: HabitRepository
) {
    // Recounts completed days from the stored history every time, so editing a past day
    // can never count the same day twice.
    suspend operator fun invoke(challengeTitle: String) {
        val masterGoal = goalRepository.getGoalByTitle(challengeTitle) ?: return

        val habits = habitRepository.getHabitsByCategory(challengeTitle)
        val subGoals = goalRepository.getGoalsByParentTitle(challengeTitle)

        val completionsByHabit = habits.associate { habit ->
            habit.id to habitRepository.getHistorySync(habit.id)
                .filter { it.amount >= habit.targetCount }
                .map { it.date }
                .toSet()
        }

        val lastDay = minOf(LocalDate.now(), masterGoal.endDate)
        var completedDays = 0
        var day = masterGoal.startDate
        while (!day.isAfter(lastDay)) {
            val habitsDone = habits.all { day in completionsByHabit.getValue(it.id) }
            if (habitsDone && subGoals.all { isSubGoalDone(it, day) }) completedDays++
            day = day.plusDays(1)
        }

        val newAmount = completedDays.toFloat().coerceAtMost(masterGoal.targetAmount)
        if (newAmount != masterGoal.currentAmount) {
            goalRepository.updateGoal(masterGoal.copy(currentAmount = newAmount))
        }
    }

    private suspend fun isSubGoalDone(subGoal: Goal, day: LocalDate): Boolean {
        if (subGoal.type != GoalType.RECURRING || subGoal.targetAmount <= 0f) {
            return subGoal.currentAmount >= subGoal.targetAmount
        }
        val zoneId = ZoneId.systemDefault()
        val start = day.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val end = day.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
        val value = goalRepository.getGoalHistoryByDate(subGoal.id, start, end)?.value ?: 0f
        return value >= subGoal.targetAmount / 3.0f
    }
}
