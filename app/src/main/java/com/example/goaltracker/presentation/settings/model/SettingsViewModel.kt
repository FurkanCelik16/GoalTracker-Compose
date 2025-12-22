package com.example.goaltracker.presentation.settings.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goaltracker.core.data.repository.GoalRepository
import com.example.goaltracker.core.data.repository.HabitRepository
import com.example.goaltracker.core.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val habitRepository: HabitRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isDarkMode = settingsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isSoundOn = settingsRepository.isSoundOn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val userName = settingsRepository.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Misafir")

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkMode(enabled) }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setSound(enabled) }
    }

    fun updateName(name: String) {
        viewModelScope.launch { settingsRepository.setUserName(name) }
    }

    fun resetAllData() {
        viewModelScope.launch {
            goalRepository.deleteAllGoals()
            habitRepository.deleteAllHabits()
        }
    }
}