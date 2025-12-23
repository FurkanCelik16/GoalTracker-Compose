package com.example.goaltracker.core.domain.usecase.goal

import com.example.goaltracker.core.domain.repository.GoalRepository
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class UpdateGoalProgressUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal, amountToAdd: Float, date: LocalDate) {
        val updatedGoal = goal.copy(currentAmount = goal.currentAmount + amountToAdd)
        repository.updateGoal(updatedGoal)

        val zoneId = ZoneId.systemDefault()
        val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

        val existingEntry = repository.getGoalHistoryByDate(goal.id, startOfDay, endOfDay)

        val historyEntity = existingEntry?.copy(
            value = existingEntry.value + amountToAdd,
            date = startOfDay
        ) ?: GoalHistoryEntity(
            goalId = goal.id,
            value = amountToAdd,
            date = startOfDay
        )

        repository.insertGoalHistory(historyEntity)
    }
}