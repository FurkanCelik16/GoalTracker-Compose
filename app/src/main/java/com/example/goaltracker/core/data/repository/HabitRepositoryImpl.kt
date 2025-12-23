package com.example.goaltracker.core.data.repository

import com.example.goaltracker.core.data.dao.EntryDao
import com.example.goaltracker.core.data.dao.HabitDao
import com.example.goaltracker.core.domain.repository.HabitRepository
import com.example.goaltracker.core.model.Habit
import com.example.goaltracker.core.model.HabitEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val entryDao: EntryDao
) : HabitRepository {

    override val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    override suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)
    override suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    override suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    override suspend fun deleteAllHabits() {
        habitDao.deleteAllHabits()
        habitDao.deleteAllEntries()
    }

    override suspend fun getHabitById(id: Int) = habitDao.getHabitById(id)

    override suspend fun getAllHabitsSync(): List<Habit> = habitDao.getAllHabits().first()

    override suspend fun getHistorySync(habitId: Int): List<HabitEntry> = entryDao.getHistorySync(habitId)

    override suspend fun insertEntry(entry: HabitEntry) = entryDao.insertEntry(entry)

    override suspend fun deleteEntry(habitId: Int, date: LocalDate) = entryDao.deleteEntry(habitId, date)

    override suspend fun deleteHabitsByCategory(category: String) = habitDao.deleteHabitsByCategory(category)

    override fun getCompletedHabitIds(date: LocalDate): Flow<List<Int>> = entryDao.getCompletedHabitIds(date)

    override suspend fun getHabitsByCategory(category: String): List<Habit> = habitDao.getHabitsByCategorySync(category)

    override fun getTotalEntryCount(): Flow<Int> = entryDao.getTotalEntryCount()

    override suspend fun updateEntryAmount(habitId: Int, date: LocalDate, newAmount: Float) = entryDao.updateEntryAmount(habitId, date, newAmount)

    override fun getEntriesByDate(date: LocalDate): Flow<List<HabitEntry>> = entryDao.getEntriesByDate(date)

    override suspend fun getEntryForDate(habitId: Int, date: LocalDate): HabitEntry? = entryDao.getEntryForDate(habitId, date)

    override fun getHistory(habitId: Int): Flow<List<HabitEntry>> = entryDao.getHistory(habitId)
}

