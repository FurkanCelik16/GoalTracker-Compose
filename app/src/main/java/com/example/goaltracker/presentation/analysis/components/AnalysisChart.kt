package com.example.goaltracker.presentation.analysis.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.max

@Composable
fun AnalysisChart(
    data: List<Pair<String, Int>>,
    targetScore: Int = 50
) {
    val maxDataScore = data.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: 1
    val rawMax = max(maxDataScore, targetScore)
    val stepCount = 4
    val rawStep = (rawMax * 1.1f) / stepCount
    val niceStep = (ceil(rawStep / 10.0) * 10).toInt().coerceAtLeast(10)
    val chartCeiling = niceStep * stepCount

    val animationProgress = remember { Animatable(0f) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val targetColor = MaterialTheme.colorScheme.error

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "Haftalık Performans",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Column(
                modifier = Modifier.fillMaxHeight().width(30.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0..stepCount) {
                    val value = chartCeiling - (niceStep * i)
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = axisColor,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val verticalPadding = 6.dp.toPx()
                val drawHeight = canvasHeight - (verticalPadding * 2)

                for (i in 0..stepCount) {
                    val y = verticalPadding + (drawHeight / stepCount * i)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (targetScore <= chartCeiling) {
                    val targetRatio = targetScore.toFloat() / chartCeiling.toFloat()
                    val targetY = verticalPadding + drawHeight - (targetRatio * drawHeight)

                    drawLine(
                        color = targetColor.copy(alpha = 0.8f),
                        start = Offset(0f, targetY),
                        end = Offset(canvasWidth, targetY),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f),
                        cap = StrokeCap.Round
                    )
                }

                val barWidth = (canvasWidth / data.size) / 2.2f
                val spacing = (canvasWidth - (barWidth * data.size)) / (data.size + 1)

                data.forEachIndexed { index, item ->
                    val score = item.second
                    val targetBarHeight = (score.toFloat() / chartCeiling.toFloat()) * drawHeight
                    val animatedBarHeight = targetBarHeight * animationProgress.value

                    val x = spacing + (index * (barWidth + spacing))
                    val yBase = verticalPadding + drawHeight
                    val yTop = yBase - animatedBarHeight

                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor, secondaryColor)
                        ),
                        topLeft = Offset(x, yTop),
                        size = Size(width = barWidth, height = animatedBarHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { item ->
                Text(
                    text = item.first,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = axisColor,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}