package com.example.goaltracker.core.data.repository

import com.example.goaltracker.core.data.dao.GoalDao
import com.example.goaltracker.core.domain.repository.GoalRepository
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
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

    override suspend fun addHistoryEntry(goalId: Int, amountToAdd: Float, date: LocalDate) {
        val zoneId = ZoneId.systemDefault()
        val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

        val existingEntry = goalDao.getGoalHistoryByDate(goalId, startOfDay, endOfDay)

        val historyEntity = existingEntry?.copy(
            value = existingEntry.value + amountToAdd,
            date = startOfDay
        ) ?: GoalHistoryEntity(
            goalId = goalId,
            value = amountToAdd,
            date = startOfDay
        )
        goalDao.insertGoalHistory(historyEntity)
    }

    override suspend fun setHistoryEntry(goalId: Int, finalAmount: Float, date: LocalDate) {
        val zoneId = ZoneId.systemDefault()
        val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

        val existingEntry = goalDao.getGoalHistoryByDate(goalId, startOfDay, endOfDay)

        val historyEntity = existingEntry?.copy(
            value = finalAmount,
            date = startOfDay
        ) ?: GoalHistoryEntity(
            goalId = goalId,
            value = finalAmount,
            date = startOfDay
        )
        goalDao.insertGoalHistory(historyEntity)
    }

    override suspend fun deleteGoalsByChallengeTitle(title: String) {
        goalDao.deleteAllChallengeRelatedGoals(title)
    }

    override suspend fun getGoalsByParentTitle(title: String): List<Goal> {
        return goalDao.getGoalsByParentTitle(title)
    }
}