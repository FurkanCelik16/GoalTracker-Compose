package com.furkancelik.goaltracker.core.domain.usecase.goal

import com.furkancelik.goaltracker.core.domain.repository.GoalRepository
import com.furkancelik.goaltracker.core.model.Goal
import javax.inject.Inject

class DeleteGoalUseCase @Inject constructor(private val repository: GoalRepository){
    suspend operator fun invoke(goal: Goal){
        repository.deleteGoal(goal)
    }
}