package com.example.goaltracker.presentation.goals.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.goaltracker.core.common.ui.components.TopBar
import com.example.goaltracker.core.model.sampleChallenges
import com.example.goaltracker.presentation.goals.components.ChallengeMasterCard
import com.example.goaltracker.presentation.goals.components.GoalCard
import com.example.goaltracker.presentation.goals.dialog.AddGoalSheet
import com.example.goaltracker.presentation.goals.model.GoalsViewModel

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel(),
    onGoalClick: (Int) -> Unit,
    onChallengeClick: (Int) -> Unit
) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopBar(title = "Hedeflerim", text = "Büyük Düşün.") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 90.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yeni Hedef")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            if (goals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Henüz bir hedefin yok. Hadi başla! 🚀",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {

                    items(
                        count = goals.size,
                        span = { index ->
                            val goal = goals[index]
                            if (goal.isChallengeMaster) GridItemSpan(2) else GridItemSpan(1)
                        }
                    ) { index ->
                        val goal = goals[index]
                        if (goal.isChallengeMaster) {
                            val matchedChallenge = sampleChallenges.find { it.title == goal.title }
                            ChallengeMasterCard(goal = goal, onItemClick = { onChallengeClick(matchedChallenge!!.id) })
                        } else {
                            GoalCard(goal = goal, onItemClick = { onGoalClick(it.id) })
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddGoalSheet(
            onDismiss = { showAddDialog = false },
            onSave = { newGoal -> viewModel.addGoal(newGoal); showAddDialog = false }
        )
    }
}