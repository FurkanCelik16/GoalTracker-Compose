package com.example.goaltracker.core.domain.usecase.challenge

import com.example.goaltracker.core.data.repository.GoalRepository
import com.example.goaltracker.core.data.repository.HabitRepository
import com.example.goaltracker.core.model.GoalType
import java.time.LocalDate
import javax.inject.Inject

class CheckChallengeProgressUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(challengeTitle: String, date: LocalDate) {
        val masterGoal = goalRepository.getGoalByTitle(challengeTitle) ?: return

        val habits = habitRepository.getHabitsByCategory(challengeTitle)

        var areHabitsDone = true
        for (habit in habits) {
            val entry = habitRepository.getEntryForDate(habit.id, date)
            if (entry == null) {
                areHabitsDone = false
                break
            }
        }

        val subGoals = goalRepository.getGoalsByParentTitle(challengeTitle)

        val areSubGoalsDone = subGoals.all { subGoal ->
            if (subGoal.type == GoalType.RECURRING && subGoal.targetAmount > 0) {
                val threshold = subGoal.targetAmount / 3.0f
                subGoal.currentAmount >= threshold && subGoal.lastUpdateDate == date
            } else {
                subGoal.currentAmount >= subGoal.targetAmount && subGoal.lastUpdateDate == date
            }
        }

        val allMet = areHabitsDone && areSubGoalsDone
        val alreadyUpdated = masterGoal.lastUpdateDate == date

        if (allMet && !alreadyUpdated) {
            val updated = masterGoal.copy(
                currentAmount = (masterGoal.currentAmount + 1).coerceAtMost(masterGoal.targetAmount),
                lastUpdateDate = date
            )
            goalRepository.updateGoal(updated)
        } else if (!allMet && alreadyUpdated) {
            val updated = masterGoal.copy(
                currentAmount = (masterGoal.currentAmount - 1).coerceAtLeast(0f),
                lastUpdateDate = date.minusDays(1)
            )
            goalRepository.updateGoal(updated)
        }
    }
}