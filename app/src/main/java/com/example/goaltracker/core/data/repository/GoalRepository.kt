package com.example.goaltracker.core.data.repository
import com.example.goaltracker.core.data.dao.GoalDao
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao
) {
    val allGoals: Flow<List<Goal>> = goalDao.getAllGoals()

    suspend fun insertGoal(goal: Goal) = goalDao.insertGoal(goal)

    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)

    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)

    suspend fun updateGoalWithHistory(goal: Goal) {
        goalDao.updateGoalWithHistory(goal)
    }
    fun getGoalHistory(goalId: Int): Flow<List<GoalHistoryEntity>> {
        return goalDao.getGoalHistory(goalId)
    }
    fun getGoalById(id: Int): Flow<Goal?> {
        return goalDao.getGoalById(id)
    }
    suspend fun getGoalByTitle(title: String): Goal? {
        return goalDao.getGoalByTitle(title)
    }

    suspend fun deleteGoalByTitle(title: String) {
        goalDao.deleteGoalByTitle(title)
    }

   suspend fun deleteAllGoals(){
        goalDao.deleteAllGoals()
    }

    suspend fun addHistoryEntry(goalId: Int, amountToAdd: Float, date: LocalDate) {
        val zoneId = ZoneId.systemDefault()

        val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

        val existingEntry = goalDao.getGoalHistoryByDate(goalId, startOfDay, endOfDay)

        val historyEntity = existingEntry?.copy(
            value = existingEntry.value + amountToAdd,
            date = startOfDay
        )
            ?: GoalHistoryEntity(
                goalId = goalId,
                value = amountToAdd,
                date = startOfDay
            )

        goalDao.insertGoalHistory(historyEntity)
    }
    suspend fun setHistoryEntry(goalId: Int, finalAmount: Float, date: LocalDate) {
        val zoneId = ZoneId.systemDefault()
        val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1

        val existingEntry = goalDao.getGoalHistoryByDate(goalId, startOfDay, endOfDay)

        val historyEntity = existingEntry?.copy(
            value = finalAmount,
            date = startOfDay
        )
            ?: GoalHistoryEntity(
                goalId = goalId,
                value = finalAmount,
                date = startOfDay
            )
        goalDao.insertGoalHistory(historyEntity)
    }
    suspend fun deleteGoalsByChallengeTitle(title: String) {
        goalDao.deleteAllChallengeRelatedGoals(title)
    }
    suspend fun getGoalsByParentTitle(title: String): List<Goal> {
        return goalDao.getGoalsByParentTitle(title)
    }
}