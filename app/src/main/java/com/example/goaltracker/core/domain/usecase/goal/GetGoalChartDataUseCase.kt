package com.example.goaltracker.core.domain.usecase.goal

import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import com.example.goaltracker.core.model.GoalType
import com.example.goaltracker.presentation.goal_detail.model.ChartTimeRange
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetGoalChartDataUseCase @Inject constructor() {

    operator fun invoke(
        history: List<GoalHistoryEntity>,
        rangeType: ChartTimeRange,
        selectedDate: LocalDate,
        goal: Goal?
    ): List<GoalHistoryEntity> {
        if (goal == null) return emptyList()

        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now()
        val isAccumulative = goal.type == GoalType.ACCUMULATIVE

        val (startDate, endDate) = if (rangeType == ChartTimeRange.WEEK) {
            selectedDate.minusDays(6) to selectedDate
        } else {
            val yearMonth = java.time.YearMonth.from(selectedDate)
            yearMonth.atDay(1) to yearMonth.atEndOfMonth()
        }

        val daysBetween = ChronoUnit.DAYS.between(startDate, endDate).toInt()
        val fullDataList = mutableListOf<GoalHistoryEntity>()
        val startMillis = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli()

        var runningTotal = if (isAccumulative) {
            history.filter { it.date < startMillis }
                .map { it.value }
                .sum()
        } else {
            0f
        }

        for (i in 0..daysBetween) {
            val dateCheck = startDate.plusDays(i.toLong())
            val dateMillis = dateCheck.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val isFuture = dateCheck.isAfter(today)

            val dailyIncrement = history.filter {
                val hDate = Instant.ofEpochMilli(it.date).atZone(zoneId).toLocalDate()
                hDate.isEqual(dateCheck)
            }.sumOf { it.value.toDouble() }.toFloat()

            val chartValue = if (isAccumulative) {
                runningTotal += dailyIncrement
                runningTotal
            } else {
                dailyIncrement
            }

            val finalValue = if (isFuture && dailyIncrement == 0f) {
                if (isAccumulative) runningTotal else 0f
            } else {
                chartValue
            }

            fullDataList.add(
                GoalHistoryEntity(
                    id = i,
                    goalId = goal.id,
                    value = finalValue,
                    date = dateMillis
                )
            )
        }
        return fullDataList
    }
}