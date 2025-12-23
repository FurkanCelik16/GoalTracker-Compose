package com.example.goaltracker.core.domain.usecase.goal

import com.example.goaltracker.core.domain.repository.GoalRepository
import com.example.goaltracker.core.model.Goal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalDetailUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke(id: Int): Flow<Goal?> {
        return repository.getGoalById(id)
    }
}