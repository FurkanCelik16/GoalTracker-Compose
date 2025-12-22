package com.example.goaltracker.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.goaltracker.core.model.Period

@Composable
fun HabitTopBar(
    selectedTab: Period,
    onTabSelected: (Period) -> Unit
) {
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant


    val startGradient = Color(0xFF7D7DD9)
    val endGradient = Color(0xFF675AE5)

    val tabs = listOf(
        "Günlük" to Period.DAILY,
        "Haftalık" to Period.WEEKLY,
        "Özel" to Period.CUSTOM
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { (label, period) ->
            val isSelected = selectedTab == period

            val selectedGradient = Brush.linearGradient(
                listOf(startGradient, endGradient)
            )
            val unselectedBrush = Brush.linearGradient(
                listOf(surfaceColor, surfaceColor)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .padding(horizontal = 4.dp)
                    .background(
                        brush = if (isSelected) selectedGradient else unselectedBrush,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else outlineColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onTabSelected(period) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) onPrimaryColor else unselectedTextColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}