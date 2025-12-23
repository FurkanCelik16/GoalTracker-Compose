package com.example.goaltracker.presentation.analysis.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goaltracker.core.common.ui.components.TopBar
import com.example.goaltracker.presentation.analysis.components.AnalysisChart
import com.example.goaltracker.presentation.analysis.components.StatCard
import com.example.goaltracker.presentation.analysis.components.WeekSelector
import com.example.goaltracker.presentation.analysis.model.AnalysisViewModel
import com.example.goaltracker.presentation.habit_detail.dialog.GoalEditDialog

@Composable
fun AnalysisScreen(
    analysisViewModel: AnalysisViewModel = hiltViewModel(),
) {
    val weeklyStats by analysisViewModel.weeklyStats.collectAsStateWithLifecycle(initialValue = emptyList())
    val targetScoreState by analysisViewModel.dailyGoal.collectAsStateWithLifecycle()
    val selectedDate by analysisViewModel.selectedDate.collectAsStateWithLifecycle()
    val totalScore by analysisViewModel.totalScore.collectAsStateWithLifecycle()
    val averageScore by analysisViewModel.averageScore.collectAsStateWithLifecycle()

    var showGoalDialog by remember { mutableStateOf(false) }

    val totalHabitsCompleted by analysisViewModel.totalCompletedCount.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopBar("İstatistiklerim", "Gelişimini Takip Et.") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AnalysisChart(data = weeklyStats, targetScore = targetScoreState)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { showGoalDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .weight(0.8f)
                        .height(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Hedef: $targetScoreState",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(modifier = Modifier.weight(1.2f)) {
                    WeekSelector(
                        currentDate = selectedDate,
                        onPrevClick = { analysisViewModel.previousWeek() },
                        onNextClick = { analysisViewModel.nextWeek() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Toplam Puan",
                    value = "$totalScore",
                    icon = Icons.Default.Star,
                    iconColor = Color(0xFFFFD700),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Ortalama",
                    value = "$averageScore",
                    icon = Icons.Default.Timeline,
                    iconColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Toplam Tamamlanan",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$totalHabitsCompleted Alışkanlık",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showGoalDialog) {
        GoalEditDialog(
            currentGoal = targetScoreState,
            onDismiss = { showGoalDialog = false },
            onSave = { newGoal ->
                analysisViewModel.saveDailyGoal(newGoal)
                showGoalDialog = false
            }
        )
    }
}