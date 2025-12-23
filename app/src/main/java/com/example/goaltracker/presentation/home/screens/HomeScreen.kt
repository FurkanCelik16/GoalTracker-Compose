package com.example.goaltracker.presentation.home.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.goaltracker.presentation.home.components.HabitContainerCard
import com.example.goaltracker.presentation.home.model.HomeViewModel
import com.example.goaltracker.presentation.navigation.GoalTrackerDestinations

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val completedIds by viewModel.completedHabitIds.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle(initialValue = emptyList())
    val entries by viewModel.dailyEntries.collectAsStateWithLifecycle()
    val streakToAnimate by viewModel.streakAnimationTrigger.collectAsStateWithLifecycle()
    val showFailureAnim by viewModel.streakFailureTrigger.collectAsStateWithLifecycle()


    HomeContent(
        streakToAnimate = streakToAnimate,
        showFailureAnim = showFailureAnim,
        onStatsClick = { navController.navigate(GoalTrackerDestinations.Stats.route) },
        onResetStreakAnim = viewModel::resetStreakAnimation,
        onResetFailureAnim = viewModel::resetFailureStreakAnimation,
        habitContainerContent = {
            HabitContainerCard(
                navController = navController,
                habits = habits,
                entries = entries,
                completedHabitIds = completedIds,
                selectedDate = selectedDate,
                onDateChanged = viewModel::selectDate,
                onToggleHabit = viewModel::toggleHabit,
                onUpdateHabit = viewModel::updateHabit,
                onDeleteHabit = viewModel::deleteHabit,
                onAddHabit = { name, category, period, difficulty, type, timeOfDay, days, interval, target ->
                    viewModel.addHabit(name, category, period, difficulty, type, timeOfDay, days, interval, target)
                }
            )
        }
    )
}