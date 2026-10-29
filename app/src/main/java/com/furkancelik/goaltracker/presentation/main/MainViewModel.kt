package com.furkancelik.goaltracker.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furkancelik.goaltracker.core.domain.repository.SettingsRepository
import com.furkancelik.goaltracker.core.domain.usecase.goal.CheckAndResetRecurringGoalsUseCase
import com.furkancelik.goaltracker.core.domain.usecase.logic.RefreshStreaksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val refreshStreaksUseCase: RefreshStreaksUseCase,
    private val checkAndResetRecurringGoalsUseCase: CheckAndResetRecurringGoalsUseCase
) : ViewModel() {

    private var lastMaintenanceDay: LocalDate? = null

    val isDarkMode = settingsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isOnboardingCompleted = settingsRepository.isOnboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.isDarkMode,
                settingsRepository.isOnboardingCompleted
            ) { _, _ ->
                false
            }.collect {
                _isLoading.value = it
            }
        }
    }

    // Runs the once-a-day work on launch and again whenever the app returns after midnight.
    fun onAppResumed() {
        val today = LocalDate.now()
        if (lastMaintenanceDay == today) return
        lastMaintenanceDay = today
        viewModelScope.launch {
            refreshStreaksUseCase()
            checkAndResetRecurringGoalsUseCase()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepository.saveOnboardingState(true)
        }
    }
}