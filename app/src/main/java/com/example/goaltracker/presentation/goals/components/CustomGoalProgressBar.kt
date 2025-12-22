package com.example.goaltracker.presentation.goals.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomGoalProgressBar(
    currentAmount: Float,
    targetAmount: Float,
    isTimeBased: Boolean = false,
    barColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val progress = (if (targetAmount > 0) currentAmount / targetAmount else 0f).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "ProgressBar"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        val density = LocalDensity.current
        val totalWidthPx = with(density) { maxWidth.toPx() }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .align(Alignment.BottomCenter)
        ) {
            val width = size.width
            val height = size.height
            val barWidth = width * animatedProgress

            drawRoundRect(
                color = trackColor.copy(alpha = 0.3f),
                size = size,
                cornerRadius = CornerRadius(100f)
            )

            drawRoundRect(
                color = barColor,
                size = Size(width = barWidth, height = height),
                cornerRadius = CornerRadius(100f)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset {
                    val xPosition = (totalWidthPx * animatedProgress).toInt()
                    val halfBubbleWidth = with(density) { 14.dp.toPx().toInt() }
                    val xOffset = xPosition - halfBubbleWidth

                    IntOffset(
                        x = xOffset.coerceAtLeast(0).coerceAtMost((totalWidthPx - halfBubbleWidth * 2).toInt()),
                        y = with(density) { -10.dp.toPx().toInt() }
                    )
                }
        ) {
            BubbleLayout(
                text = "%${(progress * 100).toInt()}",
                color = barColor,
                isTimeBased = isTimeBased
            )
        }
    }
}

@Composable
fun BubbleLayout(
    text: String,
    color: Color,
    isTimeBased: Boolean
) {
    Box(contentAlignment = Alignment.Center) {
        val pointerSize = 12f
        val cornerRadius = 12f
        Canvas(modifier = Modifier.matchParentSize()) {
            val trianglePath = Path().apply {
                moveTo(size.width / 2, size.height)
                lineTo(size.width / 2 - pointerSize, size.height - pointerSize)
                lineTo(size.width / 2 + pointerSize, size.height - pointerSize)
                close()
            }

            drawRoundRect(
                color = color,
                size = Size(size.width, size.height - pointerSize),
                cornerRadius = CornerRadius(cornerRadius)
            )
            drawPath(path = trianglePath, color = color, style = Fill)
        }

        if (isTimeBased) {
            Icon(
                imageVector = Icons.Default.HourglassTop,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .padding(bottom = 5.dp)
            )
        } else {
            Text(
                text = text,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                    .padding(bottom = 5.dp)
            )
        }
    }
}