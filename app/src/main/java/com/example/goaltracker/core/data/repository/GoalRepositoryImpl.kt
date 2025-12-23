package com.example.goaltracker.core.data.repository

import com.example.goaltracker.core.data.dao.GoalDao
import com.example.goaltracker.core.domain.repository.GoalRepository
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override val allGoals: Flow<List<Goal>> = goalDao.getAllGoals()

    override suspend fun insertGoal(goal: Goal) = goalDao.insertGoal(goal)

    override suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)

    override suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)

    override suspend fun updateGoalWithHistory(goal: Goal) {
        goalDao.updateGoalWithHistory(goal)
    }

    override fun getGoalHistory(goalId: Int): Flow<List<GoalHistoryEntity>> {
        return goalDao.getGoalHistory(goalId)
    }

    override fun getGoalById(id: Int): Flow<Goal?> {
        return goalDao.getGoalById(id)
    }

    override suspend fun getGoalByTitle(title: String): Goal? {
        return goalDao.getGoalByTitle(title)
    }

    override suspend fun deleteGoalByTitle(title: String) {
        goalDao.deleteGoalByTitle(title)
    }

    override suspend fun deleteAllGoals() {
        goalDao.deleteAllGoals()
    }

    override suspend fun getGoalHistoryByDate(goalId: Int, start: Long, end: Long): GoalHistoryEntity? {
        return goalDao.getGoalHistoryByDate(goalId, start, end)
    }

    override suspend fun insertGoalHistory(historyEntity: GoalHistoryEntity) {
        goalDao.insertGoalHistory(historyEntity)
    }

    override suspend fun deleteGoalsByChallengeTitle(title: String) {
        goalDao.deleteAllChallengeRelatedGoals(title)
    }

    override suspend fun getGoalsByParentTitle(title: String): List<Goal> {
        return goalDao.getGoalsByParentTitle(title)
    }
}