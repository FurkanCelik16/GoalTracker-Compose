package com.example.goaltracker.presentation.home.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.goaltracker.R
import com.example.goaltracker.core.common.ui.components.TopBar
import com.example.goaltracker.presentation.home.components.HabitContainerCard
import com.example.goaltracker.presentation.home.components.StreakBrokenOverlay
import com.example.goaltracker.presentation.home.components.StreakSuccessOverlay
import com.example.goaltracker.presentation.home.model.HomeViewModel
import com.example.goaltracker.presentation.navigation.GoalTrackerDestinations

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val selectedDate by viewModel.selectedDate.collectAsState()
    val completedIds by viewModel.completedHabitIds.collectAsState()

    val streakToAnimate by viewModel.streakAnimationTrigger.collectAsState()
    val showFailureAnim by viewModel.streakFailureTrigger.collectAsState()

    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary



    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                TopBar(
                    title = "Rutinlerim",
                    text = "",
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(top = 14.dp, end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(primaryColor.copy(alpha = 0.1f))
                                .clickable { navController.navigate(GoalTrackerDestinations.Stats.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.graph),
                                contentDescription = "İstatistikler",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HabitContainerCard(
                        navController = navController,
                        viewModel = viewModel,
                        completedHabitIds = completedIds,
                        selectedDate = selectedDate,
                        onDateChanged = { newDate ->
                            viewModel.selectDate(newDate)}

                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
            if (showFailureAnim) {
                Dialog(
                    onDismissRequest = { viewModel.resetFailureStreakAnimation() },
                    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
                ) {
                    StreakBrokenOverlay(isVisible = true, onAnimationFinished = { viewModel.resetFailureStreakAnimation() })
                }
            }
            if (streakToAnimate != null) {
                Dialog(
                    onDismissRequest = { viewModel.resetStreakAnimation() },
                    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
                ) {
                    StreakSuccessOverlay(isVisible = true, streak = streakToAnimate!!, onAnimationFinished = { viewModel.resetStreakAnimation() })
                }
            }
        }
    }
}