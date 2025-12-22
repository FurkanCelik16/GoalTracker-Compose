package com.example.goaltracker.core.domain.usecase.goal

import com.example.goaltracker.core.data.repository.GoalRepository
import com.example.goaltracker.core.model.Goal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor(private val repository: GoalRepository){
  operator fun invoke(): Flow<List<Goal>> = repository.allGoals
}