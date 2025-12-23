package com.example.goaltracker.core.domain.usecase.goal

import com.example.goaltracker.core.domain.repository.GoalRepository
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalType
import java.time.LocalDate
import javax.inject.Inject

class UpdateGoalProgressUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal, inputAmount: Float, date: LocalDate) {

        if (goal.type == GoalType.ACCUMULATIVE) {
            goalRepository.addHistoryEntry(goal.id, inputAmount, date)

            val newTotal = goal.currentAmount + inputAmount
            val updatedGoal = goal.copy(
                currentAmount = newTotal,
                lastUpdateDate = LocalDate.now()
            )
            goalRepository.updateGoal(updatedGoal)

        } else {
            goalRepository.setHistoryEntry(goal.id, inputAmount, date)

            if (date == LocalDate.now()) {
                val updatedGoal = goal.copy(
                    currentAmount = inputAmount,
                    lastUpdateDate = LocalDate.now()
                )
                goalRepository.updateGoal(updatedGoal)
            }
        }
    }
}