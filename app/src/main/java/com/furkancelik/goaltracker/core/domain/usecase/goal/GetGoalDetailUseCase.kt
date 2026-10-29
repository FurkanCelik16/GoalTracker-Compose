package com.furkancelik.goaltracker.core.domain.usecase.goal

import com.furkancelik.goaltracker.core.domain.repository.GoalRepository
import com.furkancelik.goaltracker.core.model.Goal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalDetailUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke(id: Int): Flow<Goal?> {
        return repository.getGoalById(id)
    }
}