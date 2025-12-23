package com.example.goaltracker.core.domain.repository

import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GoalRepository {
    val allGoals: Flow<List<Goal>>
    suspend fun insertGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
    suspend fun updateGoalWithHistory(goal: Goal)
    fun getGoalHistory(goalId: Int): Flow<List<GoalHistoryEntity>>
    fun getGoalById(id: Int): Flow<Goal?>
    suspend fun getGoalByTitle(title: String): Goal?
    suspend fun deleteGoalByTitle(title: String)
    suspend fun deleteAllGoals()

    suspend fun getGoalHistoryByDate(goalId: Int, start: Long, end: Long): GoalHistoryEntity?

    suspend fun insertGoalHistory(historyEntity: GoalHistoryEntity)
    suspend fun deleteGoalsByChallengeTitle(title: String)
    suspend fun getGoalsByParentTitle(title: String): List<Goal>
}