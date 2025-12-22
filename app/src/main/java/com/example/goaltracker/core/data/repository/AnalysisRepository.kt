package com.example.goaltracker.core.data.repository

import com.example.goaltracker.core.data.dao.EntryDao
import com.example.goaltracker.core.model.HabitEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalysisRepository @Inject constructor(
    private val entryDao: EntryDao
) {
    fun getEntriesForDateRange(start: LocalDate, end: LocalDate): Flow<List<HabitEntry>> {
        return entryDao.getEntriesForDateRange(start, end)
    }
    fun getHabitHistory(habitId: Int): Flow<List<HabitEntry>> {
        return entryDao.getHistory(habitId)
    }
}