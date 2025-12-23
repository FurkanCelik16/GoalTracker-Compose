package com.example.goaltracker.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goaltracker.R
import com.example.goaltracker.core.model.Habit
import com.example.goaltracker.core.model.HabitDifficulty
import com.example.goaltracker.core.model.HabitType
import com.example.goaltracker.core.model.TimeOfDay
import com.example.goaltracker.core.theme.*
import java.time.LocalDate

@Composable
fun HabitCard(
    habit: Habit,
    isCompleted: Boolean,
    selectedDate: LocalDate,
    currentAmount: Float,
    onToggleComplete: () -> Unit,
    onClick: (habitId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isNegative = habit.type == HabitType.NEGATIVE
    val isToday = selectedDate == LocalDate.now()
    val isFinished = isCompleted
    val isSuccessState = if (isNegative) !isFinished else isFinished

    val backgroundBrush = remember(isSuccessState, habit.type) {
        if (isSuccessState) {
            Brush.linearGradient(listOf(GreenStart, GreenEnd))
        } else {
            if (isNegative) {
                Brush.linearGradient(listOf(RedStart, RedEnd))
            } else {
                Brush.linearGradient(listOf(DarkBlue, BlueEnd))
            }
        }
    }
    val (timeIcon, timeColor) = getTimeIconAndColor(habit.timeOfDay)
    val timeText = when (habit.timeOfDay) {
        TimeOfDay.MORNING -> "Sabah"
        TimeOfDay.AFTERNOON -> "Öğle"
        TimeOfDay.EVENING -> "Akşam"
        TimeOfDay.ANYTIME -> "Tümü"
    }

    val buttonColor = if (isSuccessState) WhiteTransparent else WhiteMoreTransparent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
            .background(brush = backgroundBrush, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { onClick(habit.id) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconRes = when (habit.category) {
                    "Kişisel Gelişim" -> R.drawable.personaldevelopment
                    "Sağlık" -> R.drawable.health
                    "Spor" -> R.drawable.sport
                    "Disiplin" -> R.drawable.discipline
                    "Sosyal" -> R.drawable.social
                    "Eğitim" -> R.drawable.education
                    else -> R.drawable.others
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(WhiteTransparent, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = habit.category,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = habit.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = habit.category,
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        val (badgeColor, badgeText) = when (habit.difficulty) {
                            HabitDifficulty.EASY -> GoldColor to "Kolay"
                            HabitDifficulty.MEDIUM -> WarningColor to "Orta"
                            HabitDifficulty.HARD -> ErrorColor to "Zor"
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    color = badgeColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(0.5.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = badgeColor,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        if (habit.timeOfDay != TimeOfDay.ANYTIME) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = timeColor.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .border(0.5.dp, timeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = timeIcon,
                                        contentDescription = null,
                                        tint = timeColor,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = timeText,
                                        color = timeColor,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(8.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp).clickable { onToggleComplete() }
            ) {
                Box(
                    modifier = Modifier.size(40.dp).background(buttonColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isFinished) {
                        val symbol = if (isNegative) "X" else "✓"
                        Text(text = symbol, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    else if (habit.targetCount > 1 && isToday) {
                        val progress = currentAmount / habit.targetCount.toFloat()

                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f),
                            strokeWidth = 3.dp,
                        )
                        Text(
                            text = "${currentAmount.toInt()}/${habit.targetCount}",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                    else {
                        val symbol = if (isNegative) "✓" else "!"
                        Text(text = symbol, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}