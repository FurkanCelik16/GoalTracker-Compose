package com.example.goaltracker.core.domain.usecase.goal

import com.example.goaltracker.core.domain.repository.GoalRepository
import com.example.goaltracker.core.model.Goal
import javax.inject.Inject

class AddGoalUseCase @Inject constructor(private val repository: GoalRepository){
    suspend operator fun invoke(goal: Goal){
        if(goal.title.isBlank()) return
        repository.insertGoal(goal)
    }
}