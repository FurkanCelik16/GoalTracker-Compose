package com.example.goaltracker.core.domain.usecase.goal

import com.example.goaltracker.core.data.repository.GoalRepository
import com.example.goaltracker.core.model.GoalType
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class CheckAndResetRecurringGoalsUseCase @Inject constructor(
    private val goalRepository: GoalRepository
) {
    suspend operator fun invoke() {
        val allGoals = goalRepository.allGoals.first()
        val today = LocalDate.now()
        val goalsToReset = allGoals.filter { goal ->
            val isRecurring = goal.type == GoalType.RECURRING
            val isOutdated = goal.lastUpdateDate == null || goal.lastUpdateDate.isBefore(today)

            isRecurring && isOutdated
        }
        goalsToReset.forEach { goal ->
            val resetGoal = goal.copy(
                currentAmount = 0f,
                lastUpdateDate = today
            )
            goalRepository.updateGoal(resetGoal)
        }
    }
}