package com.example.goaltracker.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goaltracker.core.model.TimeOfDay
import java.time.LocalDate
import java.time.format.TextStyle

@Composable
fun HorizontalCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    selectedTimeOfDay: TimeOfDay,
    onTimeOfDaySelected: (TimeOfDay) -> Unit,
    modifier: Modifier = Modifier
) {
    val dates = remember {
        (0..14).map { LocalDate.now().minusDays(it.toLong()) }.reversed() +
                (1..1).map { LocalDate.now().plusDays(it.toLong()) }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        val todayIndex = dates.indexOf(LocalDate.now())
        if (todayIndex != -1) listState.scrollToItem(todayIndex)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            state = listState,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(dates) { date ->
                DateItem(
                    date = date,
                    isSelected = date == selectedDate,
                    onClick = { onDateSelected(date) }
                )
            }
        }

        VerticalDivider(
            modifier = Modifier
                .height(40.dp)
                .padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        TimeFilterButton(
            selectedTimeOfDay = selectedTimeOfDay,
            onTimeSelected = onTimeOfDaySelected
        )
    }
}

@Composable
fun TimeFilterButton(
    selectedTimeOfDay: TimeOfDay,
    onTimeSelected: (TimeOfDay) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val timeInfo = getTimeIconAndColor(selectedTimeOfDay)

    val labelText = when(selectedTimeOfDay) {
        TimeOfDay.MORNING -> "Sabah"
        TimeOfDay.AFTERNOON -> "Öğle"
        TimeOfDay.EVENING -> "Akşam"
        else -> "Tümü"
    }

    Box {
        Column(
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                .clickable { expanded = true }
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = timeInfo.first,
                contentDescription = null,
                tint = timeInfo.second,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = labelText,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seç",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp).offset(x = (-3).dp)
                )
            }
        }


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            TimeOfDay.entries.forEach { time ->
                DropdownMenuItem(
                    text = {
                        val name = when(time) {
                            TimeOfDay.MORNING -> "Sabah"
                            TimeOfDay.AFTERNOON -> "Öğle"
                            TimeOfDay.EVENING -> "Akşam"
                            else -> "Tümü"
                        }
                        Text(name)
                    },
                    leadingIcon = {
                        val iconInfo = getTimeIconAndColor(time)
                        Icon(iconInfo.first, null, tint = iconInfo.second)
                    },
                    onClick = {
                        onTimeSelected(time)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    val contentColor = if (isSelected)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurface.copy(alpha = if (isSystemInDarkTheme()) 0.6f else 1f)

    Column(
        modifier = Modifier
            .width(52.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, java.util.Locale.forLanguageTag("tr")).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}