package com.example.goaltracker.core.data.repository

import com.example.goaltracker.core.data.dao.EntryDao
import com.example.goaltracker.core.data.dao.HabitDao
import com.example.goaltracker.core.model.Habit
import com.example.goaltracker.core.model.HabitEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val habitDao: HabitDao,
    private val entryDao: EntryDao
) {

    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    suspend fun deleteAllHabits(){
        habitDao.deleteAllHabits()
        habitDao.deleteAllEntries()
    }
    suspend fun getHabitById(id: Int) = habitDao.getHabitById(id)
    suspend fun getAllHabitsSync(): List<Habit> {
        return habitDao.getAllHabits().first()
    }
    suspend fun getHistorySync(habitId: Int): List<HabitEntry> {
        return entryDao.getHistorySync(habitId)
    }
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun insertEntry(entry: HabitEntry) = entryDao.insertEntry(entry)
    suspend fun deleteEntry(habitId: Int, date: LocalDate) = entryDao.deleteEntry(habitId, date)

    suspend fun deleteHabitsByCategory(category: String) {
        habitDao.deleteHabitsByCategory(category)
    }

    fun getCompletedHabitIds(date: LocalDate): Flow<List<Int>> {
        return entryDao.getCompletedHabitIds(date)
    }

    suspend fun getHabitsByCategory(category: String): List<Habit> {
        return habitDao.getHabitsByCategorySync(category)
    }

    fun getTotalEntryCount(): Flow<Int> {
        return entryDao.getTotalEntryCount()
    }
    suspend fun updateEntryAmount(habitId: Int, date: LocalDate, newAmount: Float) {
        entryDao.updateEntryAmount(habitId, date, newAmount)
    }
    fun getEntriesByDate(date: LocalDate): Flow<List<HabitEntry>> {
        return entryDao.getEntriesByDate(date)
    }
    suspend fun getEntryForDate(habitId: Int, date: LocalDate): HabitEntry? {
        return entryDao.getEntryForDate(habitId, date)
    }
    fun getHistory(habitId: Int): Flow<List<HabitEntry>> = entryDao.getHistory(habitId)

}