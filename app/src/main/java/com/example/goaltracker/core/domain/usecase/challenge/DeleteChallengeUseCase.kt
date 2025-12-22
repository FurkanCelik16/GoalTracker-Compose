package com.example.goaltracker.core.domain.usecase.challenge

import com.example.goaltracker.core.data.repository.GoalRepository
import com.example.goaltracker.core.data.repository.HabitRepository
import javax.inject.Inject

class DeleteChallengeUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(challengeTitle: String) {
        goalRepository.deleteGoalsByChallengeTitle(challengeTitle)
        habitRepository.deleteHabitsByCategory(challengeTitle)
    }
}