package com.example.goaltracker.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.goaltracker.core.common.ui.dialog.HabitDialogController
import com.example.goaltracker.core.common.util.isDueOn
import com.example.goaltracker.core.model.*
import com.example.goaltracker.presentation.navigation.GoalTrackerDestinations
import com.example.goaltracker.presentation.home.dialog.RenderHabitDialogs
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

    val filteredHabits = remember(habits, selectedTab, selectedDate, completedHabitIds) {
        habits
            .filter { it.period == selectedTab }
            .filter { it.isDueOn(selectedDate) }
            .sortedWith(
                compareBy<Habit> { habit ->
                    val isMarked = completedHabitIds.contains(habit.id)
                    when {
                        habit.type == HabitType.POSITIVE && isMarked -> 2
                        habit.type == HabitType.NEGATIVE && isMarked -> 1
                        else -> 0
                    }
                }.thenBy { it.timeOfDay.ordinal }
            )
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
                        onDateSelected = onDateChanged,
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