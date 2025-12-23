package com.example.goaltracker.presentation.challenge.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.goaltracker.core.model.sampleChallenges
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChallengeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val challengeId: Int = checkNotNull(savedStateHandle["challengeId"])

    private val _challenge = MutableStateFlow(
        sampleChallenges.find { it.id == challengeId }
    )
    val challenge = _challenge.asStateFlow()
}