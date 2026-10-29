package com.furkancelik.goaltracker.presentation.habit_detail.dialog

import androidx.compose.runtime.Composable
import com.furkancelik.goaltracker.core.common.ui.components.DeleteConfirmDialog
import com.furkancelik.goaltracker.core.model.Habit
import com.furkancelik.goaltracker.core.model.HabitDifficulty
import com.furkancelik.goaltracker.core.model.HabitType
import com.furkancelik.goaltracker.core.model.Period
import com.furkancelik.goaltracker.core.model.TimeOfDay
import com.furkancelik.goaltracker.presentation.home.dialog.AddHabitSheet

@Composable
fun HabitDetailDialogs(
    habit: Habit,

    showPurposeDialog: Boolean,
    onDismissPurpose: () -> Unit,
    onSavePurpose: (String) -> Unit,

    showEditDialog: Boolean,
    onDismissEdit: () -> Unit,
    onSaveEdit: (String, String, Period, HabitDifficulty, HabitType, TimeOfDay, List<Int>, Int,Int) -> Unit,

    showDeleteDialog: Boolean,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    warningMessage: String?,
) {
    if (showPurposeDialog) {
        PurposeDialog(
            initialText = habit.detail,
            onDismiss = onDismissPurpose,
            onSave = onSavePurpose
        )
    }

    if (showEditDialog) {
        AddHabitSheet(
            onDismiss = onDismissEdit,
            onSave = onSaveEdit,
            isEditMode = true,
            initialName = habit.name,
            initialCategory = habit.category,
            initialPeriod = habit.period,
            initialDifficulty = habit.difficulty,
            initialType = habit.type,
            initialTimeOfDay = habit.timeOfDay,
            initialSelectedDays = habit.selectedDays,
            initialPeriodInterval = habit.periodInterval,
            initialTargetCount = habit.targetCount
        )
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            onDismissDelete = onDismissDelete,
            onConfirmDelete = onConfirmDelete,
            itemName = habit.name,
            itemType = "alışkanlığını",
            warningMessage = warningMessage
        )
    }
}