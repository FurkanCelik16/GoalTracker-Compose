package com.example.goaltracker.presentation.analysis.model

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goaltracker.core.data.repository.AnalysisRepository
import com.example.goaltracker.core.data.repository.HabitRepository
import com.example.goaltracker.core.model.HabitDifficulty
import com.example.goaltracker.core.model.HabitType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    habitRepository: HabitRepository,
    private val analysisRepository: AnalysisRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)

    private val _dailyGoal = MutableStateFlow(sharedPrefs.getInt("daily_goal", 50))
    val dailyGoal = _dailyGoal.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()
    val totalCompletedCount = habitRepository.getTotalEntryCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )
    fun saveDailyGoal(newGoal: Int) {
        sharedPrefs.edit { putInt("daily_goal", newGoal) }
        _dailyGoal.value = newGoal
    }

    fun previousWeek() { _selectedDate.value = _selectedDate.value.minusWeeks(1) }
    fun nextWeek() { _selectedDate.value = _selectedDate.value.plusWeeks(1) }

    @OptIn(ExperimentalCoroutinesApi::class)
    val weeklyStats: Flow<List<Pair<String, Int>>> = combine(
        _selectedDate,
        habitRepository.allHabits,
        _selectedDate.flatMapLatest { date ->
            analysisRepository.getEntriesForDateRange(date.minusDays(6), date)
        }
    ) { endDate, habits, entries ->
        val startDate = endDate.minusDays(6)
        val stats = mutableListOf<Pair<String, Int>>()

        for (i in 0..6) {
            val dateToCheck = startDate.plusDays(i.toLong())
            val completedIdsAtDate = entries
                .filter { it.date == dateToCheck }
                .map { it.habitId }

            val dailyScore = habits.filter { habit ->
                habit.id in completedIdsAtDate && habit.type == HabitType.POSITIVE
            }.sumOf { habit ->
                val points: Int = when(habit.difficulty) {
                    HabitDifficulty.HARD -> 20
                    HabitDifficulty.MEDIUM -> 10
                    HabitDifficulty.EASY -> 5
                }
                points
            }

            val dayName = dateToCheck.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("tr"))
            stats.add(dayName to dailyScore)
        }
        stats
    }
}