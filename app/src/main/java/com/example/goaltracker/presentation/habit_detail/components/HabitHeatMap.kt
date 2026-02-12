package com.example.goaltracker.presentation.habit_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goaltracker.core.common.ui.components.DateRangeSelector
import com.example.goaltracker.core.model.HabitType
import com.example.goaltracker.core.theme.ErrorColor
import com.example.goaltracker.core.theme.SuccessColor
import com.example.goaltracker.presentation.habit_detail.model.HabitDetailViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HabitHeatMap(
    completedDates: List<LocalDate>,
    habitType: HabitType,
    startDate: LocalDate,
    currentDate:LocalDate,
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val currentMonth = remember(currentDate) { YearMonth.from(currentDate) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value
    val today = LocalDate.now()
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val emptyDayColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val todayColor = MaterialTheme.colorScheme.surfaceVariant
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        DateRangeSelector(
            isWeekly = false,
            currentDate = selectedDate,
            onPrevClick = { viewModel.previousMonth() },
            onNextClick = { viewModel.nextMonth() },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Pzt", "Sa", "Çrş", "Prş", "Cm", "Cmt", "Pzr").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = onSurfaceVariantColor,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totalSlots = daysInMonth + (firstDayOfWeek - 1)
        val rows = (totalSlots / 7) + if (totalSlots % 7 == 0) 0 else 1

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0 until 7) {
                    val dayIndex = (row * 7) + col - (firstDayOfWeek - 1) + 1

                    if (dayIndex in 1..daysInMonth) {
                        val date = currentMonth.atDay(dayIndex)
                        val hasEntry = completedDates.contains(date)
                        val isToday = date == today
                        val isBeforeCreation = date.isBefore(startDate)

                        val (backgroundColor, contentColor, isBold) = when {
                            habitType == HabitType.POSITIVE && hasEntry ->
                                Triple(SuccessColor, onPrimaryColor, true)
                            habitType == HabitType.NEGATIVE && hasEntry ->
                                Triple(ErrorColor, onPrimaryColor, true)
                            isBeforeCreation ->
                                Triple(emptyDayColor.copy(alpha = 0.1f), onSurfaceVariantColor.copy(alpha = 0.3f), false)
                            habitType == HabitType.NEGATIVE && !hasEntry && (date.isBefore(today) || date.isEqual(today)) ->
                                Triple(SuccessColor, onPrimaryColor, true)

                            isToday -> Triple(todayColor, onSurfaceVariantColor, true)

                            else -> Triple(emptyDayColor, onSurfaceVariantColor, false)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(backgroundColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayIndex.toString(),
                                color = contentColor,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Normal
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}