package com.example.goaltracker.core.domain.repository

import com.example.goaltracker.core.model.Habit
import com.example.goaltracker.core.model.HabitEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepository {
    val allHabits: Flow<List<Habit>>
    suspend fun insertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun deleteAllHabits()
    suspend fun getHabitById(id: Int): Habit?
    suspend fun getAllHabitsSync(): List<Habit>
    suspend fun getHistorySync(habitId: Int): List<HabitEntry>
    suspend fun insertEntry(entry: HabitEntry)
    suspend fun deleteEntry(habitId: Int, date: LocalDate)
    suspend fun deleteHabitsByCategory(category: String)
    fun getCompletedHabitIds(date: LocalDate): Flow<List<Int>>
    suspend fun getHabitsByCategory(category: String): List<Habit>
    fun getTotalEntryCount(): Flow<Int>
    suspend fun updateEntryAmount(habitId: Int, date: LocalDate, newAmount: Float)
    fun getEntriesByDate(date: LocalDate): Flow<List<HabitEntry>>
    suspend fun getEntryForDate(habitId: Int, date: LocalDate): HabitEntry?
    fun getHistory(habitId: Int): Flow<List<HabitEntry>>
}