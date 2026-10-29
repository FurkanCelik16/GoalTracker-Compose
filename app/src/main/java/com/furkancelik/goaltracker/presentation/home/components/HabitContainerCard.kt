package com.furkancelik.goaltracker.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.furkancelik.goaltracker.core.common.ui.dialog.HabitDialogController
import com.furkancelik.goaltracker.core.common.util.isDueOn
import com.furkancelik.goaltracker.core.model.*
import com.furkancelik.goaltracker.presentation.navigation.GoalTrackerDestinations
import com.furkancelik.goaltracker.presentation.home.dialog.RenderHabitDialogs
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitContainerCard(
    navController: NavController,
    habits: List<Habit>,
    entries: Map<Int, HabitEntry>,
    completedHabitIds: List<Int>,
    selectedDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
    onToggleHabit: (Habit) -> Unit,
    onAddHabit: (String, String, Period, HabitDifficulty, HabitType, TimeOfDay, List<Int>, Int, Int) -> Unit,
    onUpdateHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit
) {
    var selectedTab by remember { mutableStateOf(Period.DAILY) }
    var selectedTimeOfDay by remember {
        mutableStateOf(getInitialTimeOfDay())
    }
    val handleDateChange = { newDate: LocalDate ->
        onDateChanged(newDate)


        selectedTimeOfDay = if (newDate.isBefore(LocalDate.now())) {
            TimeOfDay.ANYTIME
        } else {
            getInitialTimeOfDay()
        }
    }
    val filteredHabits = remember(habits, selectedTab, selectedDate, completedHabitIds, selectedTimeOfDay) {
        habits
            .filter { it.period == selectedTab }
            .filter { it.isDueOn(selectedDate) }
            .filter {
                if (selectedTimeOfDay == TimeOfDay.ANYTIME) return@filter true
                it.timeOfDay == selectedTimeOfDay || it.timeOfDay == TimeOfDay.ANYTIME
            }
            .sortedWith(
                compareBy<Habit> { habit ->
                    val isCompleted = completedHabitIds.contains(habit.id)
                    when {
                        !isCompleted && habit.type == HabitType.POSITIVE -> 0
                        !isCompleted && habit.type == HabitType.NEGATIVE -> 1
                        else -> 2
                    }
                }.thenBy { habit ->
                    if(selectedTimeOfDay == TimeOfDay.ANYTIME){
                        if (habit.timeOfDay == TimeOfDay.ANYTIME) 0 else 1
                    }
                    else{
                        if (habit.timeOfDay == selectedTimeOfDay) 0 else 1
                    }
                }
                    .thenBy { it.timeOfDay.ordinal }
                    .thenBy { it.id }
            )
    }

    val hiddenByTimeCount = remember(habits, selectedTab, selectedDate, selectedTimeOfDay) {
        if (selectedTimeOfDay == TimeOfDay.ANYTIME) 0
        else habits.count {
            it.period == selectedTab && it.isDueOn(selectedDate) &&
                it.timeOfDay != selectedTimeOfDay && it.timeOfDay != TimeOfDay.ANYTIME
        }
    }

    val dialogController = remember { HabitDialogController() }
    val containerColor = MaterialTheme.colorScheme.surfaceContainer
    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val showBorder = containerColor.luminance() > 0.5f

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
            shape = RoundedCornerShape(24.dp),
            color = containerColor,
            border = if (showBorder) BorderStroke(2.dp, borderColor) else null,
            tonalElevation = 0.dp
        ) {
            LazyColumn(
                modifier = Modifier.wrapContentHeight(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HorizontalCalendar(
                        selectedDate = selectedDate,
                        onDateSelected = handleDateChange,
                        selectedTimeOfDay = selectedTimeOfDay,
                        onTimeOfDaySelected = { selectedTimeOfDay = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                }
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HabitTopBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
                    }
                }
                if (hiddenByTimeCount > 0) {
                    item {
                        TimeFilterHint(
                            hiddenCount = hiddenByTimeCount,
                            timeOfDay = selectedTimeOfDay,
                            onShowAll = { selectedTimeOfDay = TimeOfDay.ANYTIME },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        AddHabitCard(onClick = { dialogController.openAddDialog() })
                    }
                }

                if (filteredHabits.isEmpty()) {
                    item {
                        EmptyStateView(modifier = Modifier.padding(top = 32.dp))
                    }
                } else {
                    items(items = filteredHabits, key = { it.id }) { habit ->
                        val isCompleted = completedHabitIds.contains(habit.id)

                        Box(modifier = Modifier.padding(horizontal = 16.dp).animateItem()) {
                            HabitCard(
                                habit = habit,
                                isCompleted = isCompleted,
                                currentAmount = entries[habit.id]?.amount ?: 0f,
                                selectedDate = selectedDate,
                                onToggleComplete = { onToggleHabit(habit) },
                                onClick = {
                                    navController.navigate(GoalTrackerDestinations.HabitDetail.createRoute(habit.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    RenderHabitDialogs(
        controller = dialogController,
        onAddHabit = onAddHabit,
        onEditHabit = onUpdateHabit,
        onDeleteHabit = onDeleteHabit
    )
}
@Composable
private fun TimeFilterHint(
    hiddenCount: Int,
    timeOfDay: TimeOfDay,
    onShowAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = when (timeOfDay) {
        TimeOfDay.MORNING -> "Sabah"
        TimeOfDay.AFTERNOON -> "Öğle"
        TimeOfDay.EVENING -> "Akşam"
        TimeOfDay.ANYTIME -> "Tümü"
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label filtresi: $hiddenCount rutin gizli",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onShowAll) {
            Text("Tümünü gör")
        }
    }
}

private fun getInitialTimeOfDay(): TimeOfDay {
    val hour = java.time.LocalTime.now().hour
    return when (hour) {
        in 5..11 -> TimeOfDay.MORNING
        in 12..16 -> TimeOfDay.AFTERNOON
        in 17..23, in 0..4 -> TimeOfDay.EVENING
        else -> TimeOfDay.ANYTIME
    }
}