package com.furkancelik.goaltracker.core.domain.usecase.goal

import com.furkancelik.goaltracker.core.domain.repository.GoalRepository
import com.furkancelik.goaltracker.core.model.Goal
import java.time.LocalDate
import javax.inject.Inject

class AddGoalUseCase @Inject constructor(private val repository: GoalRepository){
    suspend operator fun invoke(goal: Goal){
        if(goal.title.isBlank()) return
        // Marks the goal as current for today so the daily reset does not wipe first-day progress.
        repository.insertGoal(goal.copy(lastUpdateDate = goal.lastUpdateDate ?: LocalDate.now()))
    }
}