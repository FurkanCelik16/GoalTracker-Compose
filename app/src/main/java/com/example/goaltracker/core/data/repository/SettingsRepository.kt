package com.example.goaltracker.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val isdarkmode = booleanPreferencesKey("is_dark_mode")
    private val issoundon = booleanPreferencesKey(
        "is_sound_on")
    private val username = stringPreferencesKey("user_name")

    val isOnBoardingCompleted = booleanPreferencesKey("is_onboarding_completed")


    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[isOnBoardingCompleted] ?: false
        }

    suspend fun saveOnboardingState(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isOnBoardingCompleted] = completed
        }
    }

    val isDarkMode: Flow<Boolean?> = context.dataStore.data
        .map { preferences -> preferences[isdarkmode] }

    val isSoundOn: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[issoundon] ?: true }

    val userName: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[username] ?: "Misafir" }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[isdarkmode] = enabled }
    }

    suspend fun setSound(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[issoundon] = enabled }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences -> preferences[username] = name }
    }
}