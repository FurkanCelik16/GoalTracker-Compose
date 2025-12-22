package com.example.goaltracker.presentation.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.goaltracker.core.model.TimeOfDay



@Composable
fun getTimeIconAndColor(time: TimeOfDay): Pair<ImageVector, Color> {

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    return when (time) {
        TimeOfDay.MORNING -> Icons.Default.WbSunny to Color(0xFFFFF59D)

        TimeOfDay.AFTERNOON -> Icons.Default.WbTwilight to Color(0xFFFFCC80)

        TimeOfDay.EVENING -> Icons.Default.Bedtime to Color(0xFFE1BEE7)

        TimeOfDay.ANYTIME -> Icons.Default.AccessTime to onSurfaceColor.copy(alpha = 0.6f)
    }
}