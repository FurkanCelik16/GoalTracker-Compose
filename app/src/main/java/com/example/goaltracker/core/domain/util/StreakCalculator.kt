package com.example.goaltracker.core.domain.util

import com.example.goaltracker.core.common.util.isDueOn
import com.example.goaltracker.core.model.Habit
import com.example.goaltracker.core.model.HabitType
import java.time.LocalDate

object StreakCalculator {

    fun calculate(habit: Habit, completedDates: List<LocalDate>): Int {
        var streak = 0
        var dateToCheck = LocalDate.now()
        val earliestCompletion = completedDates.minOrNull()

        val effectiveStartDate = if (earliestCompletion != null && earliestCompletion.isBefore(habit.startDate)) {
            earliestCompletion
        } else {
            habit.startDate
        }

        while (dateToCheck >= effectiveStartDate) {

            val isDue = habit.isDueOn(dateToCheck)

            if (isDue) {
                val isCompleted = completedDates.contains(dateToCheck)

                if (habit.type == HabitType.POSITIVE) {
                    if (isCompleted) {
                        streak++
                    } else {
                        if (dateToCheck == LocalDate.now()) {
                            // Pas geç
                        } else {
                            break
                        }
                    }
                } else {
                    if (!isCompleted) {
                        streak++
                    } else {
                        break
                    }
                }
            }
            dateToCheck = dateToCheck.minusDays(1)
        }

        return streak
    }
}