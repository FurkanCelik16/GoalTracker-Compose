package com.example.goaltracker.presentation.analysis.model

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goaltracker.core.domain.usecase.analysis.CalculateWeeklyStatsUseCase
import com.example.goaltracker.core.domain.usecase.analysis.GetTotalCompletedCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    calculateWeeklyStatsUseCase: CalculateWeeklyStatsUseCase,
    getTotalCompletedCountUseCase: GetTotalCompletedCountUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)

    private val _dailyGoal = MutableStateFlow(sharedPrefs.getInt("daily_goal", 50))
    val dailyGoal = _dailyGoal.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    val totalCompletedCount = getTotalCompletedCountUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    val weeklyStats: Flow<List<Pair<String, Int>>> = calculateWeeklyStatsUseCase(_selectedDate)

    fun saveDailyGoal(newGoal: Int) {
        sharedPrefs.edit { putInt("daily_goal", newGoal) }
        _dailyGoal.value = newGoal
    }

    val totalScore = weeklyStats.map { stats ->
        stats.sumOf { it.second }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val averageScore = weeklyStats.map { stats ->
        if (stats.isNotEmpty()) stats.sumOf { it.second } / stats.size else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun previousWeek() { _selectedDate.value = _selectedDate.value.minusWeeks(1) }
    fun nextWeek() { _selectedDate.value = _selectedDate.value.plusWeeks(1) }
}