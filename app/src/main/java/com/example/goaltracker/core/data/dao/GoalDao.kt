package com.example.goaltracker.core.data.dao

import androidx.room.*
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY endDate ASC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE id = :id")
    fun getGoalById(id: Int): Flow<Goal?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: GoalHistoryEntity)

    @Update
    suspend fun updateHistory(history: GoalHistoryEntity)

    @Query("SELECT * FROM goal_history WHERE goalId = :goalId AND date = :date LIMIT 1")
    suspend fun getHistoryByDate(goalId: Int, date: Long): GoalHistoryEntity?

    @Query("SELECT * FROM goal_history WHERE goalId = :goalId ORDER BY date ASC")
    fun getGoalHistory(goalId: Int): Flow<List<GoalHistoryEntity>>

    @Query("SELECT * FROM goals WHERE title = :title LIMIT 1")
    suspend fun getGoalByTitle(title: String): Goal?

    @Query("DELETE FROM goals WHERE title = :title")
    suspend fun deleteGoalByTitle(title: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalHistory(history: GoalHistoryEntity)

    @Query("SELECT * FROM goal_history WHERE goalId = :goalId AND date BETWEEN :startDate AND :endDate LIMIT 1")
    suspend fun getGoalHistoryByDate(goalId: Int, startDate: Long, endDate: Long): GoalHistoryEntity?

    @Query("DELETE FROM goals WHERE title = :title OR parentChallengeTitle = :title")
    suspend fun deleteAllChallengeRelatedGoals(title: String)

    @Query("SELECT * FROM goals WHERE parentChallengeTitle = :title")
    suspend fun getGoalsByParentTitle(title: String): List<Goal>


    @Transaction
    suspend fun updateGoalWithHistory(goal: Goal) {
        updateGoal(goal)
        val today = getStartOfDay(System.currentTimeMillis())

        val existingHistory = getHistoryByDate(goal.id, today)

        if (existingHistory != null) {
            updateHistory(existingHistory.copy(value = goal.currentAmount))
        } else {
            insertHistory(
                GoalHistoryEntity(
                    goalId = goal.id,
                    value = goal.currentAmount,
                    date = today
                )
            )
        }
    }
}
fun getStartOfDay(timestamp: Long): Long {
    val calendar = java.util.Calendar.getInstance()
    calendar.timeInMillis = timestamp
    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
    calendar.set(java.util.Calendar.MINUTE, 0)
    calendar.set(java.util.Calendar.SECOND, 0)
    calendar.set(java.util.Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}