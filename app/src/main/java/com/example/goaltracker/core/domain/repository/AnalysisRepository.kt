package com.example.goaltracker.core.domain.repository

import com.example.goaltracker.core.model.HabitEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AnalysisRepository {
    fun getEntriesForDateRange(start: LocalDate, end: LocalDate): Flow<List<HabitEntry>>
    fun getHabitHistory(habitId: Int): Flow<List<HabitEntry>>
}