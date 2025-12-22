package com.example.goaltracker.core.domain.usecase.goal

import com.example.goaltracker.core.data.repository.GoalRepository
import com.example.goaltracker.core.model.GoalHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalHistoryUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke(goalId: Int): Flow<List<GoalHistoryEntity>> {
        return repository.getGoalHistory(goalId)
    }
}