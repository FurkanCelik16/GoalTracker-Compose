package com.example.goaltracker.presentation.goal_detail.components

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goaltracker.core.model.GoalHistoryEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import androidx.compose.ui.graphics.toArgb

@Composable
fun GoalProgressChart(
    history: List<GoalHistoryEntity>,
    targetAmount: Float,
    modifier: Modifier = Modifier
) {
    if (history.isEmpty()) return

    val axisFormatter = remember { DateTimeFormatter.ofPattern("dd", Locale.forLanguageTag("tr")) }
    val tooltipFormatter = remember { DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("tr")) }

    val sortedHistory = remember(history) { history.sortedBy { it.date } }

    val processedData = remember(sortedHistory) {
        sortedHistory.map { entity ->
            val localDate = Instant.ofEpochMilli(entity.date).atZone(ZoneId.systemDefault()).toLocalDate()
            Triple(
                localDate.format(axisFormatter),
                entity.value,
                localDate.format(tooltipFormatter)
            )
        }
    }

    var selectedIndex by remember { mutableIntStateOf(-1) }

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(processedData) {
        selectedIndex = -1
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    val maxDataValue = processedData.maxOfOrNull { it.second }?.takeIf { it > 0f } ?: 1f
    val rawMax = max(maxDataValue, targetAmount)
    val stepCount = 4

    val rawStep = (rawMax * 1.1f) / stepCount

    val magnitude = 10.0.pow(floor(log10(rawStep.toDouble())))

    val normalizedStep = rawStep / magnitude
    val niceNormalizedStep = when {
        normalizedStep <= 1.0 -> 1.0
        normalizedStep <= 2.0 -> 2.0
        normalizedStep <= 5.0 -> 5.0
        else -> 10.0
    }

    val niceStep = (niceNormalizedStep * magnitude).toFloat()
    val chartCeiling = niceStep * stepCount
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val targetLineColor = MaterialTheme.colorScheme.error
    val tooltipBgColor = MaterialTheme.colorScheme.inverseSurface
    val tooltipTextColor = MaterialTheme.colorScheme.inverseOnSurface.toArgb()
    val scrubberColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    val canvasTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f).toArgb()

    val density = LocalDensity.current

    val textPaint = remember(density, canvasTextColor) {
        Paint().apply {
            color = canvasTextColor
            textAlign = Paint.Align.CENTER
            textSize = density.run { 10.sp.toPx() }
            isAntiAlias = true
        }
    }

    val tooltipTextPaint = remember(density, tooltipTextColor) {
        Paint().apply {
            color = tooltipTextColor
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(220.dp)) {

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(35.dp)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0..stepCount) {
                    val value = (chartCeiling - (niceStep * i))

                    val textValue = if (value % 1.0 == 0.0) {
                        val intVal = value.toInt()
                        if (intVal >= 10000) "${intVal / 1000}k" else intVal.toString()
                    } else {
                        String.format(Locale.US, "%.1f", value)
                    }

                    Text(
                        text = textValue,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = axisColor,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(processedData) {
                        fun updateSelection(x: Float, width: Float) {
                            val dataCount = processedData.size
                            if (dataCount == 0) return
                            val slotWidth = width / dataCount
                            val index = (x / slotWidth).toInt()
                            selectedIndex = index.coerceIn(0, dataCount - 1)
                        }

                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: continue
                                if (change.pressed) {
                                    updateSelection(change.position.x, size.width.toFloat())
                                }
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val bottomLabelHeight = 20.dp.toPx()
                val verticalPadding = 12.dp.toPx()
                val drawHeight = canvasHeight - bottomLabelHeight - verticalPadding

                for (i in 0..stepCount) {
                    val y = verticalPadding + (drawHeight / stepCount * i)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (targetAmount <= chartCeiling && targetAmount > 0) {
                    val targetRatio = targetAmount / chartCeiling
                    val targetY = verticalPadding + drawHeight - (targetRatio * drawHeight)
                    drawLine(
                        color = targetLineColor.copy(alpha = 0.8f),
                        start = Offset(0f, targetY),
                        end = Offset(canvasWidth, targetY),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f),
                        cap = StrokeCap.Round
                    )
                }

                val dataCount = processedData.size
                val spacePerItem = canvasWidth / dataCount
                val barWidth = (spacePerItem * 0.6f).coerceAtMost(40.dp.toPx())

                processedData.forEachIndexed { index, item ->
                    val value = item.second
                    val targetBarHeight = (value / chartCeiling) * drawHeight

                    val minBarHeight = if (value > 0) 4.dp.toPx() else 0f
                    val finalBarHeight = (targetBarHeight * animationProgress.value).coerceAtLeast(minBarHeight)

                    val xCenter = (spacePerItem * index) + (spacePerItem / 2)
                    val xLeft = xCenter - (barWidth / 2)
                    val yBase = verticalPadding + drawHeight
                    val yTop = yBase - finalBarHeight

                    val isSelected = index == selectedIndex
                    val alpha = if (selectedIndex == -1 || isSelected) 1.0f else 0.4f

                    if (isSelected) {
                        drawLine(
                            color = scrubberColor,
                            start = Offset(xCenter, 0f),
                            end = Offset(xCenter, canvasHeight),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    if (value > 0) {
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(primaryColor.copy(alpha = alpha), secondaryColor.copy(alpha = alpha))
                            ),
                            topLeft = Offset(xLeft, yTop),
                            size = Size(width = barWidth, height = finalBarHeight),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                    }

                    val shouldShow = if (dataCount > 10) {
                        val isFirst = index == 0
                        val isLast = index == dataCount - 1
                        val isMultipleOfFive = (index + 1) % 5 == 0
                        val isSecondToLast = index == dataCount - 2
                        isFirst || isLast || (isMultipleOfFive && !isSecondToLast)
                    } else {
                        true
                    }

                    if (shouldShow) {
                        drawIntoCanvas {
                            it.nativeCanvas.drawText(item.first, xCenter, canvasHeight, textPaint)
                        }
                    }

                    if (isSelected) {
                        val valueText = if (value % 1.0 == 0.0) value.toInt().toString() else String.format(Locale.US, "%.1f", value)
                        val tooltipText = "${item.third}: $valueText"

                        val bounds = Rect()
                        tooltipTextPaint.getTextBounds(tooltipText, 0, tooltipText.length, bounds)
                        val textWidth = bounds.width()
                        val textHeight = bounds.height()

                        val padding = 8.dp.toPx()
                        val tooltipWidth = textWidth + (padding * 2)
                        val tooltipHeight = textHeight + (padding * 2)

                        val tooltipX = (xCenter - (tooltipWidth / 2)).coerceIn(0f, canvasWidth - tooltipWidth)

                        var tooltipY = yTop - tooltipHeight - 10.dp.toPx()
                        var isTooltipAbove = true

                        if (tooltipY < 0) {
                            tooltipY = yTop + 10.dp.toPx()
                            isTooltipAbove = false
                        }

                        drawRoundRect(
                            color = tooltipBgColor,
                            topLeft = Offset(tooltipX, tooltipY),
                            size = Size(tooltipWidth, tooltipHeight),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )

                        val path = Path().apply {
                            if (isTooltipAbove) {
                                moveTo(xCenter, yTop - 2.dp.toPx())
                                lineTo(xCenter - 6.dp.toPx(), tooltipY + tooltipHeight)
                                lineTo(xCenter + 6.dp.toPx(), tooltipY + tooltipHeight)
                            } else {
                                moveTo(xCenter, yTop + 2.dp.toPx())
                                lineTo(xCenter - 6.dp.toPx(), tooltipY)
                                lineTo(xCenter + 6.dp.toPx(), tooltipY)
                            }
                            close()
                        }
                        drawPath(path, tooltipBgColor)

                        drawIntoCanvas {
                            val textY = tooltipY + (tooltipHeight / 2) + (textHeight / 2) - 4f
                            it.nativeCanvas.drawText(
                                tooltipText,
                                tooltipX + (tooltipWidth / 2),
                                textY,
                                tooltipTextPaint
                            )
                        }
                    }
                }
            }
        }
    }
}