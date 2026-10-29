package com.furkancelik.goaltracker.core.common.util

import com.furkancelik.goaltracker.core.model.Habit
import com.furkancelik.goaltracker.core.model.Period
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
            // Guards against habits saved with an interval of 0 before validation existed.
            daysBetween % periodInterval.coerceAtLeast(1) == 0L
        }
    }
}