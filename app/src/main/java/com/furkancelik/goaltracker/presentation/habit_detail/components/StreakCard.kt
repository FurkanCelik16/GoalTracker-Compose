package com.furkancelik.goaltracker.presentation.habit_detail.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.furkancelik.goaltracker.core.model.Period
import com.furkancelik.goaltracker.core.theme.WarningColor

@Composable
fun StreakCard(streak: Int,period: Period) {

    val unitSuffix = remember(period) {
        when (period) {
            Period.WEEKLY -> "Hafta"
            else -> "Gün"
        }
    }

    val message = remember(streak) {
        when {
            streak >= 30 -> "YANIYOSUN FUAT ABİİ! \uD83D\uDD25\uD83D\uDD25"
            streak >= 21 -> "Durdurulamıyorsun! 🚀"
            streak >= 7 -> "Efsanesin. Devam ET! \uD83C\uDFC6"
            streak >= 3 -> "Seri Yakalandı! ⚡"
            streak >= 1 -> "Harika Başlangıç! ✨"
            else -> "Neyi Bekliyorsun? \uD83E\uDD14"
        }
    }

    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val highlightColor = WarningColor

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mevcut Seri",
                    style = MaterialTheme.typography.labelMedium,
                    color = subTextColor,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))


                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = highlightColor
                )
            }

            Text(
                "$streak $unitSuffix",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor
            )
        }
    }
}