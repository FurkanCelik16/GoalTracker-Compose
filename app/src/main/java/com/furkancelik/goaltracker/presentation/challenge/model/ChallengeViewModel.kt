package com.furkancelik.goaltracker.presentation.challenge.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furkancelik.goaltracker.core.model.Challenge
import com.furkancelik.goaltracker.core.model.Goal
import com.furkancelik.goaltracker.core.model.GoalType
import com.furkancelik.goaltracker.core.model.Habit
import com.furkancelik.goaltracker.core.model.Period
import com.furkancelik.goaltracker.core.model.sampleChallenges
import com.furkancelik.goaltracker.core.domain.repository.GoalRepository
import com.furkancelik.goaltracker.core.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _challenges = MutableStateFlow(sampleChallenges)
    val challenges = _challenges.asStateFlow()

    val activeChallengeTitles = goalRepository.allGoals
        .map { list ->
            list
                .filter { it.isChallenge }
                .map { it.title }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startChallenge(challenge: Challenge, onComplete: () -> Unit) {
        viewModelScope.launch {
            val endDate = LocalDate.now().plusDays(challenge.days.toLong())

            val masterGoal = Goal(
                title = challenge.title,
                targetAmount = challenge.days.toFloat(),
                currentAmount = 0f,
                type = GoalType.ACCUMULATIVE,
                iconIndex = 0,
                endDate = endDate,
                isChallengeMaster = true,
                parentChallengeTitle = null,
                isChallenge = true,
                startDate = LocalDate.now()
            )
            goalRepository.insertGoal(masterGoal)

            challenge.goalsToAdd.forEach { blueprint ->
                if (blueprint.type == GoalType.BINARY) {
                    val newHabit = Habit(
                        name = blueprint.title,
                        category = challenge.title,
                        period = Period.DAILY,
                        detail = "${challenge.days} günlük mücadelenin bir parçası.",
                        isChallenge = true
                    )
                    habitRepository.insertHabit(newHabit)
                } else {
                    val newSubGoal = Goal(
                        title = blueprint.title,
                        targetAmount = blueprint.targetAmount,
                        currentAmount = 0f,
                        type = blueprint.type,
                        iconIndex = blueprint.iconIndex,
                        endDate = endDate,
                        isChallengeMaster = false,
                        parentChallengeTitle = challenge.title,
                        startDate = LocalDate.now(),
                        lastUpdateDate = LocalDate.now()
                    )
                    goalRepository.insertGoal(newSubGoal)
                }
            }
            onComplete()
        }
    }

    fun cancelChallenge(challenge: Challenge, onComplete: () -> Unit) {
        viewModelScope.launch {
            // Deletes by parent link so the user's own goals with the same name survive.
            goalRepository.deleteGoalsByChallengeTitle(challenge.title)
            habitRepository.deleteHabitsByCategory(challenge.title)

            onComplete()
        }
    }
}