package com.example.goaltracker.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.goaltracker.core.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {
    private val isdarkmode = booleanPreferencesKey("is_dark_mode")
    private val issoundon = booleanPreferencesKey("is_sound_on")
    private val username = stringPreferencesKey("user_name")
    private val isOnBoardingCompletedKey = booleanPreferencesKey("is_onboarding_completed")

    override val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[isOnBoardingCompletedKey] ?: false }

    override val isDarkMode: Flow<Boolean?> = context.dataStore.data
        .map { preferences -> preferences[isdarkmode] }

    override val isSoundOn: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[issoundon] ?: true }

    override val userName: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[username] ?: "Misafir" }

    override suspend fun saveOnboardingState(completed: Boolean) {
        context.dataStore.edit { preferences -> preferences[isOnBoardingCompletedKey] = completed }
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[isdarkmode] = enabled }
    }

    override suspend fun setSound(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[issoundon] = enabled }
    }

    override suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences -> preferences[username] = name }
    }
}