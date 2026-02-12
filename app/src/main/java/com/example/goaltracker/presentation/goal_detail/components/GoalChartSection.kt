package com.example.goaltracker.presentation.goal_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goaltracker.core.model.GoalHistoryEntity
import com.example.goaltracker.presentation.goal_detail.model.ChartTimeRange
import com.example.goaltracker.core.common.ui.components.DateRangeSelector
import java.time.LocalDate

@Composable
fun GoalChartSection(
    history: List<GoalHistoryEntity>,
    targetAmount: Float,
    isRecurring: Boolean,
    timeRange: ChartTimeRange,
    selectedDate: LocalDate,
    onRangeSelected: (ChartTimeRange) -> Unit,
    onDateChanged: (LocalDate) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRecurring) "Günlük Performans" else "İlerleme Tarihçesi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ChartTimeRange.entries.forEach { range ->
                        val isSelected = timeRange == range
                        val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(containerColor)
                                .clickable { onRangeSelected(range) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = range.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = contentColor
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            DateRangeSelector(
                isWeekly = timeRange == ChartTimeRange.WEEK,
                currentDate = selectedDate,
                onPrevClick = {
                    onDateChanged(if (timeRange == ChartTimeRange.WEEK) selectedDate.minusWeeks(1) else selectedDate.minusMonths(1))
                },
                onNextClick = {
                    onDateChanged(if (timeRange == ChartTimeRange.WEEK) selectedDate.plusWeeks(1) else selectedDate.plusMonths(1))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GoalProgressChart(
                history = history,
                targetAmount = targetAmount,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}