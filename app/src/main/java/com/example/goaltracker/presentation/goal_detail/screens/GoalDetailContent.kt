package com.example.goaltracker.presentation.goal_detail.screens

import BinaryGoalCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.goaltracker.R
import com.example.goaltracker.core.model.Goal
import com.example.goaltracker.core.model.GoalHistoryEntity
import com.example.goaltracker.core.model.GoalType
import com.example.goaltracker.core.model.ReminderType
import com.example.goaltracker.presentation.goal_detail.components.*
import com.example.goaltracker.presentation.goal_detail.model.ChartTimeRange
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailContent(
    goal: Goal?,
    chartData: List<GoalHistoryEntity>,
    timeRange: ChartTimeRange,
    chartDate: LocalDate,
    inputAmount: String,
    selectedEntryDate: LocalDate,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReminderClick: () -> Unit,
    onChartRangeSelected: (ChartTimeRange) -> Unit,
    onChartDateChanged: (LocalDate) -> Unit,
    onInputValueChange: (String) -> Unit,
    onInputDateClick: () -> Unit,
    onSaveProgress: () -> Unit,
    onUndoClick: () -> Unit,
    onUpdateBinaryProgress: (Float) -> Unit
) {
    Scaffold(
        topBar = {
            goal?.let { currentGoal ->
                GoalDetailTopBar(
                    title = currentGoal.title,
                    onBackClick = onBackClick,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onReminderClick = onReminderClick,
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
            val isBinary = goal.type == GoalType.BINARY
            val isAccumulative = goal.type == GoalType.ACCUMULATIVE
            val isRecurring = goal.type == GoalType.RECURRING
            val isCompleted = goal.currentAmount >= goal.targetAmount

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
                        onUpdateBinaryProgress(if (isCompleted) 0f else 1f)
                    }
                } else {
                    if (isAccumulative && isCompleted) {
                        SuccessCard(
                            message = stringResource(id = R.string.congratulations),
                            subMessage = stringResource(id = R.string.goal_completed),
                            onUndo = onUndoClick
                        )
                    }

                    GoalChartSection(
                        history = chartData,
                        targetAmount = goal.targetAmount,
                        isRecurring = isRecurring,
                        timeRange = timeRange,
                        selectedDate = chartDate,
                        onRangeSelected = onChartRangeSelected,
                        onDateChanged = onChartDateChanged
                    )

                    GoalStatsSection(
                        currentAmount = if (isRecurring) inputAmount.toFloatOrNull() ?: 0f else goal.currentAmount,
                        targetAmount = goal.targetAmount,
                        isRecurring = isRecurring,
                        isAccumulative = isAccumulative,
                        isCompleted = isCompleted,
                        inputAmount = inputAmount,
                        onInputValueChange = { floatValue ->
                            onInputValueChange(floatValue.toString())
                        }
                    )
                    HorizontalDivider()

                    GoalInputSection(
                        isAccumulative = isAccumulative,
                        inputAmount = inputAmount,
                        onInputChange = onInputValueChange,
                        selectedDate = selectedEntryDate,
                        onDateClick = onInputDateClick,
                        onSaveClick = onSaveProgress
                    )
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}