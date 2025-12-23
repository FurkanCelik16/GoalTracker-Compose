package com.example.goaltracker.presentation.habit_detail.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.goaltracker.core.common.ui.dialog.HabitDialogController
import com.example.goaltracker.presentation.habit_detail.components.DetailTopBar
import com.example.goaltracker.presentation.habit_detail.components.HeatMapCard
import com.example.goaltracker.presentation.habit_detail.components.PurposeCard
import com.example.goaltracker.presentation.habit_detail.components.StreakCard
import com.example.goaltracker.presentation.habit_detail.dialog.HabitDetailDialogs
import com.example.goaltracker.presentation.home.model.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    navController: NavController,
    habitId: Int,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val habitState by homeViewModel.getHabitFlow(habitId).collectAsStateWithLifecycle(initialValue = null)
    val historyState by homeViewModel.getHabitHistory(habitId).collectAsStateWithLifecycle(initialValue = emptyList())

    val backgroundColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.background
    } else {
        Color(0xFFF5F5F7)
    }

    val dialogController = remember { HabitDialogController() }
    var deleteWarningMessage by remember { mutableStateOf<String?>(null) }

    if (habitState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val habit = habitState!!
    val completedDates = historyState.map { it.date }

    Scaffold(
        topBar = {
            DetailTopBar(
                habitName = habit.name,
                onBackClick = { navController.popBackStack() },
                onEditClick = { dialogController.openEditDialog(habit) },
                onDeleteClick = {
                    deleteWarningMessage = if (habit.isChallenge) {
                        "\nBu alışkanlık '${habit.category}' Mücadelesinin bir parçasıdır." +
                                " \n\nBunu silersen tüm mücadeleyi (hedefler dahil) silmiş olursun!"
                    } else {
                        null
                    }
                    dialogController.openDeleteConfirm(habit)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            StreakCard(streak = habit.streak, period = habit.period)

            Spacer(modifier = Modifier.height(16.dp))
            HeatMapCard(
                habit = habit,
                completedDates = completedDates
            )

            Spacer(modifier = Modifier.height(16.dp))

            PurposeCard(
                detail = habit.detail,
                onEditClick = { dialogController.openAddDialog() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    HabitDetailDialogs(
        habit = dialogController.selectedHabit ?: habit,
        warningMessage = deleteWarningMessage,
        showPurposeDialog = dialogController.isAddOpen,
        onDismissPurpose = dialogController::closeAddDialog,
        onSavePurpose = { newText ->
            homeViewModel.updateHabit(habit.copy(detail = newText))
            dialogController.closeAddDialog()
        },
        showEditDialog = dialogController.isEditOpen,
        onDismissEdit = dialogController::closeEditDialog,
        onSaveEdit = { name, category, period, difficulty, type, timeOfDay, days, interval, target ->
            homeViewModel.updateHabit(
                habit.copy(
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
            dialogController.closeEditDialog()
        },
        showDeleteDialog = dialogController.isDeleteOpen,
        onDismissDelete = dialogController::closeDeleteConfirm,
        onConfirmDelete = {
            homeViewModel.deleteHabit(habit)
            dialogController.closeDeleteConfirm()
            navController.popBackStack()
        }
    )
}