package com.example.goaltracker.core.domain.usecase.logic

import com.example.goaltracker.core.data.repository.HabitRepository
import com.example.goaltracker.core.domain.util.StreakCalculator
import javax.inject.Inject

class RefreshStreaksUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke() {
        val habits = repository.getAllHabitsSync()

        habits.forEach { habit ->
            val history = repository.getHistorySync(habit.id)
            val completedDates = history.map { it.date }

            val correctStreak = StreakCalculator.calculate(
                habit = habit,
                completedDates = completedDates
            )

            if (habit.streak != correctStreak) {
                repository.updateHabit(habit.copy(streak = correctStreak))
            }
        }
    }
}