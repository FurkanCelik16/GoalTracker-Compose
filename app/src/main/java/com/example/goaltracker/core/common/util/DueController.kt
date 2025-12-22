package com.example.goaltracker.core.common.util

import com.example.goaltracker.core.model.Habit
import com.example.goaltracker.core.model.Period
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun Habit.isDueOn(date: LocalDate): Boolean {

    return when (period) {
        Period.DAILY -> true
        Period.WEEKLY -> {
            val dayOfWeek = date.dayOfWeek.value
            selectedDays.contains(dayOfWeek)
        }
        Period.CUSTOM -> {
            val daysBetween = ChronoUnit.DAYS.between(startDate, date)
            daysBetween % periodInterval == 0L
        }
    }
}