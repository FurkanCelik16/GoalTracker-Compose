package com.example.goaltracker.presentation.home.dialog

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.goaltracker.core.common.ui.dialog.HabitDialogController
import com.example.goaltracker.core.model.Habit
import com.example.goaltracker.core.model.HabitDifficulty
import com.example.goaltracker.core.model.HabitType
import com.example.goaltracker.core.model.Period
import com.example.goaltracker.core.model.TimeOfDay

@Composable
fun RenderHabitDialogs(
    controller: HabitDialogController,
    onAddHabit: (String, String, Period, HabitDifficulty, HabitType, TimeOfDay, List<Int>, Int,Int) -> Unit,
    onEditHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
) {
    if (controller.isAddOpen) {
        AddHabitSheet(
            onDismiss = { controller.closeAddDialog() },
            onSave = { name, category, period, difficulty, type, timeOfDay, days, interval,target ->
                onAddHabit(name, category, period, difficulty, type, timeOfDay, days, interval,target)
            }
        )
    }
    if (controller.isEditOpen && controller.selectedHabit != null) {
        val habitToEdit = controller.selectedHabit!!

        AddHabitSheet(
            isEditMode = true,
            initialName = habitToEdit.name,
            initialCategory = habitToEdit.category,
            initialPeriod = habitToEdit.period,
            initialDifficulty = habitToEdit.difficulty,
            initialType = habitToEdit.type,
            initialTimeOfDay = habitToEdit.timeOfDay,
            initialSelectedDays = habitToEdit.selectedDays,
            initialPeriodInterval = habitToEdit.periodInterval,
            onDismiss = { controller.closeEditDialog() },
            onSave = { name, category, period, difficulty, type, timeOfDay, days, interval,target ->
                onEditHabit(
                    habitToEdit.copy(
                        name = name,
                        category = category,
                        period = period,
                        difficulty = difficulty,
                        type = type,
                        timeOfDay = timeOfDay,
                        selectedDays = days,
                        periodInterval = interval,
                        targetCount = target
                    )
                )
                controller.closeEditDialog()
            }
        )
    }
    if (controller.isDeleteOpen && controller.selectedHabit != null) {
        AlertDialog(
            onDismissRequest = { controller.closeDeleteConfirm() },
            title = { Text("Silmek İstiyor musun?", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("${controller.selectedHabit!!.name} alışkanlığı silinecek.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteHabit(controller.selectedHabit!!)
                        controller.closeDeleteConfirm()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sil", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { controller.closeDeleteConfirm() }) {
                    Text("İptal", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}