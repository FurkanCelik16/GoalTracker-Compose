package com.example.goaltracker.presentation.goal_detail.screens

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goaltracker.R
import com.example.goaltracker.core.common.util.formatAmount
import com.example.goaltracker.core.model.GoalType
import com.example.goaltracker.core.model.ReminderType
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
    val goal by viewModel.goal.collectAsStateWithLifecycle()
    val chartData by viewModel.chartData.collectAsStateWithLifecycle()
    val timeRange by viewModel.chartTimeRange.collectAsStateWithLifecycle()
    val chartDate by viewModel.chartSelectedDate.collectAsStateWithLifecycle()

    var inputAmount by remember { mutableStateOf("") }
    var selectedEntryDate by remember { mutableStateOf(LocalDate.now()) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showExactAlarmPermissionDialog by remember { mutableStateOf(false) }
    var deleteWarningMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showReminderDialog = true
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis <= System.currentTimeMillis()
        }
    )

    LaunchedEffect(selectedEntryDate, goal) {
        val currentGoal = goal ?: return@LaunchedEffect
        if (currentGoal.type == GoalType.RECURRING) {
            val amountOnDate = viewModel.getProgressForDate(selectedEntryDate)
            inputAmount = amountOnDate.formatAmount()
        }
    }

    GoalDetailContent(
        goal = goal,
        chartData = chartData,
        timeRange = timeRange,
        chartDate = chartDate,
        inputAmount = inputAmount,
        selectedEntryDate = selectedEntryDate,
        onBackClick = onBackClick,
        onEditClick = { showEditDialog = true },
        onDeleteClick = {
            val currentGoal = goal ?: return@GoalDetailContent
            val isPart = currentGoal.parentChallengeTitle != null
            val isFlagged = currentGoal.isChallenge || currentGoal.isChallengeMaster

            if (isPart || isFlagged) {
                val name = currentGoal.parentChallengeTitle ?: currentGoal.title
                deleteWarningMessage = context.getString(R.string.challenge_delete_warning, name)
            } else {
                deleteWarningMessage = null
            }
            showDeleteDialog = true
        },
        onReminderClick = {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                showReminderDialog = true
            } else {
                showRationaleDialog = true
            }
        },
        onChartRangeSelected = { viewModel.updateChartRange(it) },
        onChartDateChanged = { viewModel.updateChartDate(it) },
        onInputValueChange = { inputAmount = it },
        onInputDateClick = { showDatePicker = true },
        onSaveProgress = {
            goal?.let { currentGoal ->
                inputAmount.toFloatOrNull()?.let { amount ->
                    viewModel.addProgress(currentGoal, amount, selectedEntryDate)
                    if (currentGoal.type != GoalType.RECURRING) {
                        inputAmount = ""
                    }
                }
            }
        },
        onUndoClick = {
            goal?.let { viewModel.updateProgress(it.targetAmount - 1f) }
        },
        onUpdateBinaryProgress = { viewModel.updateProgress(it) }
    )

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

    if (showExactAlarmPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showExactAlarmPermissionDialog = false },
            title = { Text(stringResource(id = R.string.alarm_permission_title)) },
            text = { Text(stringResource(id = R.string.alarm_permission_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showExactAlarmPermissionDialog = false
                    val intent = android.content.Intent(
                        android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        "package:${context.packageName}".toUri()
                    )
                    context.startActivity(intent)
                }) {
                    Text(stringResource(id = R.string.open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExactAlarmPermissionDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    if (showReminderDialog && goal != null) {
        GoalReminderDialog(
            currentGoal = goal!!,
            onDismiss = { showReminderDialog = false },
            onSave = { type, start, end, interval ->
                if (type == ReminderType.NONE) {
                    viewModel.setReminder(goal!!, type, start, end, interval)
                    showReminderDialog = false
                    return@GoalReminderDialog
                }
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (!alarmManager.canScheduleExactAlarms()) {
                    showExactAlarmPermissionDialog = true
                    return@GoalReminderDialog
                }

                viewModel.setReminder(goal!!, type, start, end, interval)
                showReminderDialog = false
            }
        )
    }
}