package com.example.goaltracker.presentation.goals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun GoalCard(
    goal: Goal,
    onItemClick: (Goal) -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM", Locale.forLanguageTag("tr")) }
    val dateText = remember(goal.endDate) { goal.endDate.format(dateFormatter) }

    val isBinary = goal.type == GoalType.BINARY
    val isCompleted = goal.currentAmount >= goal.targetAmount

    val progress = if (isBinary) {
        val totalDays = ChronoUnit.DAYS.between(goal.startDate, goal.endDate).toFloat().coerceAtLeast(1f)
        val daysPassed = ChronoUnit.DAYS.between(goal.startDate, LocalDate.now()).toFloat().coerceAtLeast(0f)
        (daysPassed / totalDays).coerceIn(0f, 1f)
    } else {
        if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).coerceIn(0f, 1f) else 0f
    }

    val (iconVector, iconColor) = if (goal.iconIndex in goalIcons.indices) {
        goalIcons[goal.iconIndex]
    } else {
        Icons.Default.Star to primaryColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 220.dp)
            .padding(vertical = 8.dp)
            .clickable { onItemClick(goal) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                VisualProgressIndicator(
                    isBinary = isBinary,
                    isCompleted = isCompleted,
                    progress = progress,
                    activeColor = iconColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                CustomGoalProgressBar(
                    currentAmount = if (isBinary) progress else goal.currentAmount,
                    targetAmount = if (isBinary) 1f else goal.targetAmount,
                    barColor = iconColor,
                    isTimeBased = isBinary,
                    trackColor = onSurfaceVariant.copy(alpha = 0.2f),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val leftText = if (isBinary) {
                        val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), goal.endDate).toInt().coerceAtLeast(0)
                        "$daysLeft Gün Kaldı"
                    } else {
                        val currentStr = if(goal.currentAmount % 1 == 0f) goal.currentAmount.toInt().toString() else goal.currentAmount.toString()
                        val targetStr = if(goal.targetAmount % 1 == 0f) goal.targetAmount.toInt().toString() else goal.targetAmount.toString()
                        "$currentStr / $targetStr"
                    }

                    Text(
                        text = leftText,
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant
                    )

                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun VisualProgressIndicator(
    isBinary: Boolean,
    isCompleted: Boolean,
    progress: Float,
    activeColor: Color
) {
    val indicatorSize = 42.dp
    val iconSize = 24.dp

    Box(
        modifier = Modifier.size(indicatorSize),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = activeColor.copy(alpha = 0.2f),
            strokeWidth = 4.dp,
        )
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = activeColor,
            strokeWidth = 4.dp,
            strokeCap = StrokeCap.Round
        )

        if (isBinary) {
            val icon = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty
            val tint = if (isCompleted) MaterialTheme.colorScheme.tertiary else activeColor

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        } else {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = activeColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}