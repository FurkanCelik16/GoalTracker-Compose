package com.furkancelik.goaltracker.core.domain.usecase.goal

import com.furkancelik.goaltracker.core.domain.repository.GoalRepository
import com.furkancelik.goaltracker.core.model.GoalHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalHistoryUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke(goalId: Int): Flow<List<GoalHistoryEntity>> {
        return repository.getGoalHistory(goalId)
    }
}