package com.example.goaltracker.presentation.navigation

sealed class GoalTrackerDestinations(val route: String) {

    data object Home : GoalTrackerDestinations("home_route")
    data object Stats : GoalTrackerDestinations("stats_route")
    data object Settings : GoalTrackerDestinations("settings_route")
    data object Goals : GoalTrackerDestinations("goals_route")
    data object Challenges : GoalTrackerDestinations("challenges_route")

    data object Splash : GoalTrackerDestinations("splash_route")
    data object Onboarding : GoalTrackerDestinations("onboarding_route")

    data object HabitDetail : GoalTrackerDestinations("habit_detail_route/{habitId}") {
        fun createRoute(habitId: Int) = "habit_detail_route/$habitId"
    }

    data object GoalDetail : GoalTrackerDestinations("goal_detail/{goalId}") {
        fun createRoute(goalId: Int) = "goal_detail/$goalId"
    }

    data object ChallengeDetail : GoalTrackerDestinations("challenge_detail/{challengeId}") {
        fun createRoute(challengeId: Int) = "challenge_detail/$challengeId"
    }
}