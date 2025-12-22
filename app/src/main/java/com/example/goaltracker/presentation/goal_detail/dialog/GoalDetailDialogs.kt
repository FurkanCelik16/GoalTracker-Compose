package com.example.goaltracker.presentation.goal_detail.dialog

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.goaltracker.core.common.ui.components.DeleteConfirmDialog
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.presentation.goals.dialog.AddGoalSheet
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailDialogs(
    goal: Goal?,
    showDatePicker: Boolean,
    showEditDialog: Boolean,
    showDeleteDialog: Boolean,
    warningMessage: String?,
    datePickerState: DatePickerState,
    onDismissDatePicker: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDismissEdit: () -> Unit,
    onEditSave: (Goal) -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismissDatePicker,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                    onDismissDatePicker()
                }) { Text("Tamam") }
            },
            dismissButton = {
                TextButton(onClick = onDismissDatePicker) { Text("İptal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEditDialog && goal != null) {
        AddGoalSheet(
            onDismiss = onDismissEdit,
            isEditMode = true,
            initialGoal = goal,
            onSave = onEditSave
        )
    }

    if (showDeleteDialog && goal != null) {
        DeleteConfirmDialog(
            onDismissDelete = onDismissDelete,
            onConfirmDelete = onConfirmDelete,
            itemName = goal.title,
            itemType = "hedefini",
            warningMessage = warningMessage
        )
    }
}