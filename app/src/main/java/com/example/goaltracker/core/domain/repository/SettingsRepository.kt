package com.example.goaltracker.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isOnboardingCompleted: Flow<Boolean>
    val isDarkMode: Flow<Boolean?>
    val isSoundOn: Flow<Boolean>
    val userName: Flow<String>
    suspend fun saveOnboardingState(completed: Boolean)
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setSound(enabled: Boolean)
    suspend fun setUserName(name: String)
}