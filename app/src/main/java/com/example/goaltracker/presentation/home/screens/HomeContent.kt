package com.example.goaltracker.presentation.home.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.goaltracker.R
import com.example.goaltracker.core.common.ui.components.TopBar
import com.example.goaltracker.presentation.home.components.StreakBrokenOverlay
import com.example.goaltracker.presentation.home.components.StreakSuccessOverlay

@Composable
fun HomeContent(
    streakToAnimate: Int?,
    showFailureAnim: Boolean,
    onStatsClick: () -> Unit,
    onResetStreakAnim: () -> Unit,
    onResetFailureAnim: () -> Unit,
    habitContainerContent: @Composable () -> Unit
) {
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
                                .clickable { onStatsClick() },
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
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    habitContainerContent()
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
            if (showFailureAnim) {
                Dialog(
                    onDismissRequest = onResetFailureAnim,
                    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
                ) {
                    StreakBrokenOverlay(isVisible = true, onAnimationFinished = onResetFailureAnim)
                }
            }
            if (streakToAnimate != null) {
                Dialog(
                    onDismissRequest = onResetStreakAnim,
                    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
                ) {
                    StreakSuccessOverlay(isVisible = true, streak = streakToAnimate, onAnimationFinished = onResetStreakAnim)
                }
            }
        }
    }
}