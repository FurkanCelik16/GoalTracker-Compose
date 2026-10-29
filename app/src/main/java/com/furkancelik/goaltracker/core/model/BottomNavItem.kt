package com.furkancelik.goaltracker.core.model


import com.furkancelik.goaltracker.R
import com.furkancelik.goaltracker.presentation.navigation.GoalTrackerDestinations

sealed class BottomNavItem(
    val title: String,
    val route: String,
    val iconRes: Int
) {
    data object Home : BottomNavItem("Rutinler", GoalTrackerDestinations.Home.route, R.drawable.renewal)
    data object Goals : BottomNavItem("Hedefler", GoalTrackerDestinations.Goals.route, R.drawable.checklist)
    data object Challenges : BottomNavItem("Mücadele", "challenges_route", R.drawable.trophy)
    data object Settings : BottomNavItem("Ayarlar", GoalTrackerDestinations.Settings.route, R.drawable.gear)
}