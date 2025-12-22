package com.example.goaltracker.presentation.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HabitChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    compact: Boolean = false
) {
    val targetBackgroundColor = if (selected) {
        color.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
    }

    val targetBorderColor = if (selected) {
        color
    } else {
        Color.Transparent
    }

    val targetTextColor = if (selected) {
        color
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val backgroundColor by animateColorAsState(targetValue = targetBackgroundColor, label = "bg")
    val borderColor by animateColorAsState(targetValue = targetBorderColor, label = "border")
    val textColor by animateColorAsState(targetValue = targetTextColor, label = "text")


    val verticalPadding = if (compact) 6.dp else 10.dp
    val horizontalPadding = if (compact) 8.dp else 16.dp
    val fontSize = if (compact) 12.sp else 14.sp
    val cornerRadius = if (compact) 8.dp else 12.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .clickable { onClick() }
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = fontSize,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            textAlign = TextAlign.Center
        )
    }
}