package com.example.goaltracker.presentation.goals.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goaltracker.core.domain.usecase.goal.AddGoalUseCase
import com.example.goaltracker.core.domain.usecase.goal.GetGoalsUseCase
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.sampleChallenges
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    getGoalsUseCase: GetGoalsUseCase,
    private val addGoalUseCase: AddGoalUseCase
) : ViewModel() {

    val goals = getGoalsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            addGoalUseCase(goal)
        }
    }
    fun getChallengeIdByTitle(title: String): Int? {
        return sampleChallenges.find { it.title == title }?.id
    }



}