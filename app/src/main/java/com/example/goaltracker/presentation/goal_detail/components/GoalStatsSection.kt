package com.example.goaltracker.presentation.goal_detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goaltracker.core.common.util.formatAmount
import com.example.goaltracker.presentation.goals.components.CustomGoalProgressBar
import com.example.goaltracker.presentation.goals.components.KSlider
import kotlin.math.roundToInt

@Composable
fun GoalStatsSection(
    currentAmount: Float,
    targetAmount: Float,
    isRecurring: Boolean,
    isAccumulative: Boolean,
    isCompleted: Boolean,
    inputAmount: String,
    onInputValueChange: (Float) -> Unit
) {
    val isDecimalGoal = remember(targetAmount) { targetAmount % 1 != 0f }
    val sliderValue = inputAmount.toFloatOrNull() ?: 0f
    val formattedCurrentValue = remember(sliderValue, isDecimalGoal) {
        if (isDecimalGoal) {
            "$sliderValue"
        } else {
            "${sliderValue.toInt()}"
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = if (isRecurring) "Bugünkü Durum" else "Toplam İlerleme",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val percentage = if (targetAmount > 0) {
                ((currentAmount / targetAmount) * 100).toInt().coerceAtMost(100)
            } else 0

            Text(
                text = "%$percentage",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (isRecurring && isCompleted) {
            Text(
                text = "Günlük hedefini tutturdun! 🔥",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isAccumulative) {
            CustomGoalProgressBar(
                currentAmount = currentAmount,
                targetAmount = targetAmount,
                barColor = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${currentAmount.formatAmount()} / ${targetAmount.formatAmount()}",
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            KSlider(
                value = sliderValue,
                onValueChange = { newValue ->
                    val snappedValue = if (isDecimalGoal) {
                        ((newValue * 10).roundToInt() / 10.0).toFloat()
                    } else {
                        newValue.roundToInt().toFloat()
                    }
                    onInputValueChange(snappedValue)
                },
                valueRange = 0f..targetAmount,
                primaryColor = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = formattedCurrentValue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "$formattedCurrentValue / ${targetAmount.formatAmount()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}