package com.example.goaltracker.core.domain.usecase.goal

import com.example.goaltracker.core.domain.repository.GoalRepository
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import com.example.goaltracker.core.model.GoalType
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class UpdateGoalProgressUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(
        goal: Goal,
        amount: Float,
        date: LocalDate,
        shouldOverwrite: Boolean = false
    ) {
        val zoneId = ZoneId.systemDefault()
        val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

        val existingEntry = repository.getGoalHistoryByDate(goal.id, startOfDay, endOfDay)
        val currentHistoryValue = existingEntry?.value ?: 0f

        val finalAmount = if (shouldOverwrite) {
            amount
        } else {
            currentHistoryValue + amount
        }
        val isToday = date == LocalDate.now()

        val shouldUpdateMainGoal = if (goal.type == GoalType.RECURRING) {
            isToday
        } else {
            true
        }

        if (shouldUpdateMainGoal) {
            val newGoalAmount = if (shouldOverwrite) {
                amount
            } else {
                goal.currentAmount + amount
            }

            repository.updateGoal(goal.copy(currentAmount = newGoalAmount))
        }

        val historyEntity = existingEntry?.copy(
            value = finalAmount,
            date = startOfDay
        ) ?: GoalHistoryEntity(
            goalId = goal.id,
            value = finalAmount,
            date = startOfDay
        )

        repository.insertGoalHistory(historyEntity)
    }
}