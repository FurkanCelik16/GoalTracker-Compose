package com.example.goaltracker.core.domain.usecase.logic

import com.example.goaltracker.core.domain.repository.HabitRepository
import com.example.goaltracker.core.domain.util.StreakCalculator
import com.example.goaltracker.core.model.Habit
import com.example.goaltracker.core.model.HabitEntry
import java.time.LocalDate
import javax.inject.Inject

class ToggleHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit, date: LocalDate) {
        val existingEntry = habitRepository.getEntryForDate(habit.id, date)

        if (habit.targetCount > 1) {
            if (existingEntry == null) {
                habitRepository.insertEntry(HabitEntry(habitId = habit.id, date = date, amount = 1f))
            } else {
                if (existingEntry.amount < habit.targetCount) {
                    habitRepository.updateEntryAmount(habit.id, date, existingEntry.amount + 1f)
                } else {
                    habitRepository.deleteEntry(habit.id, date)
                }
            }
        } else {
            if (existingEntry != null) {
                habitRepository.deleteEntry(habit.id, date)
            } else {
                habitRepository.insertEntry(HabitEntry(habitId = habit.id, date = date))
            }
        }
        val history = habitRepository.getHistorySync(habit.id)
        val completedDates = history.filter { it.amount >= habit.targetCount }.map { it.date }
        val newStreak = StreakCalculator.calculate(
            habit = habit,
            completedDates = completedDates
        )
        habitRepository.updateHabit(habit.copy(streak = newStreak))
    }
}

