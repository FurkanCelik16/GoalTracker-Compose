package com.example.goaltracker.presentation.goal_detail.screens

import BinaryGoalCard
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.goaltracker.core.common.util.formatAmount
import com.example.goaltracker.core.model.GoalType
import com.example.goaltracker.core.model.ReminderType
import com.example.goaltracker.presentation.goal_detail.components.*
import com.example.goaltracker.presentation.goal_detail.dialog.GoalDetailDialogs
import com.example.goaltracker.presentation.goal_detail.dialog.GoalReminderDialog
import com.example.goaltracker.presentation.goal_detail.dialog.PermissionRationaleDialog
import com.example.goaltracker.presentation.goal_detail.model.GoalDetailViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    onBackClick: () -> Unit,
    viewModel: GoalDetailViewModel = hiltViewModel()
) {
    val goal by viewModel.goal.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val timeRange by viewModel.chartTimeRange.collectAsState()
    val chartDate by viewModel.chartSelectedDate.collectAsState()
    val history by viewModel.history.collectAsState()

    var inputAmount by remember { mutableStateOf("") }
    var selectedEntryDate by remember { mutableStateOf(LocalDate.now()) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }

    var showRationaleDialog by remember { mutableStateOf(false) }

    var deleteWarningMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showReminderDialog = true
        } else {
            //To-Do
        }
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis <= System.currentTimeMillis()
        }
    )

    LaunchedEffect(selectedEntryDate, history, goal) {
        val currentGoal = goal ?: return@LaunchedEffect

        if (currentGoal.type == GoalType.RECURRING) {
            val amountOnDate = viewModel.getProgressForDate(selectedEntryDate)
            inputAmount = amountOnDate.formatAmount()
        }
    }

    Scaffold(
        topBar = {
            goal?.let { currentGoal ->
                GoalDetailTopBar(
                    title = currentGoal.title,
                    onBackClick = onBackClick,
                    onEditClick = { showEditDialog = true },
                    onDeleteClick = {
                        val isPart = currentGoal.parentChallengeTitle != null
                        val isFlagged = currentGoal.isChallenge || currentGoal.isChallengeMaster

                        if (isPart || isFlagged) {
                            val name = currentGoal.parentChallengeTitle ?: currentGoal.title
                            deleteWarningMessage = "\nBu hedef, '$name' Mücadelesinin bir parçasıdır. \n\nBunu silersen tüm mücadeleyi iptal etmiş olursun!"
                        } else {
                            deleteWarningMessage = null
                        }
                        showDeleteDialog = true
                    },
                    onReminderClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            showReminderDialog = true
                        } else {
                            showRationaleDialog = true
                        }
                    },
                    hasReminder = currentGoal.reminderType != ReminderType.NONE
                )
            }
        }
    ) { padding ->
        if (goal == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val currentGoal = goal!!
            val isBinary = currentGoal.type == GoalType.BINARY
            val isAccumulative = currentGoal.type == GoalType.ACCUMULATIVE
            val isRecurring = currentGoal.type == GoalType.RECURRING
            val isCompleted = currentGoal.currentAmount >= currentGoal.targetAmount

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                if (isBinary) {
                    BinaryGoalCard(isCompleted = isCompleted) {
                        viewModel.updateProgress(if (isCompleted) 0f else 1f)
                    }
                } else {
                    if (isAccumulative && isCompleted) {
                        SuccessCard(
                            message = "Tebrikler!",
                            subMessage = "Hedef tamamlandı.",
                            onUndo = { viewModel.updateProgress(currentGoal.targetAmount - 1f) }
                        )
                    }

                    GoalChartSection(
                        history = chartData,
                        targetAmount = currentGoal.targetAmount,
                        isRecurring = isRecurring,
                        timeRange = timeRange,
                        selectedDate = chartDate,
                        onRangeSelected = { viewModel.updateChartRange(it) },
                        onDateChanged = { viewModel.updateChartDate(it) }
                    )

                    GoalStatsSection(
                        currentAmount = if(isRecurring) inputAmount.toFloatOrNull() ?: 0f else currentGoal.currentAmount,
                        targetAmount = currentGoal.targetAmount,
                        isRecurring = isRecurring,
                        isAccumulative = isAccumulative,
                        isCompleted = isCompleted,
                        inputAmount = inputAmount,
                        onInputValueChange = { newValue ->
                            inputAmount = newValue.toString()
                        }
                    )

                    HorizontalDivider()

                    GoalInputSection(
                        isAccumulative = isAccumulative,
                        inputAmount = inputAmount,
                        onInputChange = { inputAmount = it },
                        selectedDate = selectedEntryDate,
                        onDateClick = { showDatePicker = true },
                        onSaveClick = {
                            inputAmount.toFloatOrNull()?.let { amount ->
                                viewModel.addProgress(currentGoal, amount, selectedEntryDate)

                                if (!isRecurring) {
                                    inputAmount = ""
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
    if (showRationaleDialog) {
        PermissionRationaleDialog(
            onConfirm = {
                showRationaleDialog = false
                permissionLauncher.launch(POST_NOTIFICATIONS)
            },
            onDismiss = { showRationaleDialog = false }
        )
    }

    GoalDetailDialogs(
        goal = goal,
        showDatePicker = showDatePicker,
        showEditDialog = showEditDialog,
        showDeleteDialog = showDeleteDialog,
        warningMessage = deleteWarningMessage,
        datePickerState = datePickerState,
        onDismissDatePicker = { showDatePicker = false },
        onDateSelected = { selectedEntryDate = it },
        onDismissEdit = { showEditDialog = false },
        onEditSave = {
            viewModel.updateGoal(it)
            showEditDialog = false
        },
        onDismissDelete = { showDeleteDialog = false },
        onConfirmDelete = {
            viewModel.deleteGoal { onBackClick() }
            showDeleteDialog = false
        }
    )
    if (showReminderDialog && goal != null) {
        GoalReminderDialog(
            currentGoal = goal!!,
            onDismiss = { showReminderDialog = false },
            onSave = { type, start, end, interval ->
                viewModel.setReminder(goal!!, type, start, end, interval)
                showReminderDialog = false
            }
        )
    }
}