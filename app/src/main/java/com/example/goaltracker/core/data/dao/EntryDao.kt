package com.example.goaltracker.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goaltracker.core.model.HabitEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: HabitEntry)

    @Query("DELETE FROM habit_entries WHERE habitId = :habitId AND date = :date")
    suspend fun deleteEntry(habitId: Int, date: LocalDate)

    @Query("SELECT habitId FROM habit_entries WHERE date = :date")
    fun getCompletedHabitIds(date: LocalDate): Flow<List<Int>>

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND date = :date")
    suspend fun getEntryForDate(habitId: Int, date: LocalDate): HabitEntry?

    @Query("SELECT COUNT(*) FROM habit_entries")
    fun getTotalEntryCount(): Flow<Int>

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId")
    fun getHistory(habitId: Int): Flow<List<HabitEntry>>

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId")
    suspend fun getHistorySync(habitId: Int): List<HabitEntry>

    @Query("SELECT * FROM habit_entries WHERE date BETWEEN :startDate AND :endDate")
    fun getEntriesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<HabitEntry>>

    @Query("UPDATE habit_entries SET amount = :newAmount WHERE habitId = :habitId AND date = :date")
    suspend fun updateEntryAmount(habitId: Int, date: LocalDate, newAmount: Float)

    @Query("SELECT * FROM habit_entries WHERE date = :date")
    fun getEntriesByDate(date: LocalDate): Flow<List<HabitEntry>>


}