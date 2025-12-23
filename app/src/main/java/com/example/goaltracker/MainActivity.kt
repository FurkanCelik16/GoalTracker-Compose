package com.example.goaltracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.goaltracker.core.common.ui.components.BottomMenu
import com.example.goaltracker.core.theme.GoalTrackerTheme
import com.example.goaltracker.presentation.analysis.screens.AnalysisScreen
import com.example.goaltracker.presentation.challenge.screens.ChallengeDetailScreen
import com.example.goaltracker.presentation.challenge.screens.ChallengeScreen
import com.example.goaltracker.presentation.goal_detail.screens.GoalDetailScreen
import com.example.goaltracker.presentation.goals.screen.GoalsScreen
import com.example.goaltracker.presentation.habit_detail.screens.HabitDetailScreen
import com.example.goaltracker.presentation.home.screens.HomeScreen
import com.example.goaltracker.presentation.main.MainViewModel
import com.example.goaltracker.presentation.navigation.GoalTrackerDestinations
import com.example.goaltracker.presentation.onboarding.OnboardingScreen
import com.example.goaltracker.presentation.settings.screens.SettingsScreen
import com.example.goaltracker.presentation.splash.AppSplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            mainViewModel.isLoading.value
        }
        setContent {
            val systemTheme = isSystemInDarkTheme()
            val isDarkModeConfig by mainViewModel.isDarkMode.collectAsStateWithLifecycle()
            val useDarkTheme = isDarkModeConfig ?: systemTheme
            val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

            GoalTrackerTheme(darkTheme = useDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val noBottomBarRoutes = listOf(
                    GoalTrackerDestinations.Splash.route,
                    GoalTrackerDestinations.Onboarding.route
                )
                val showBottomBar = currentRoute !in noBottomBarRoutes

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomMenu(navController = navController)
                        }
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = GoalTrackerDestinations.Splash.route,
                        modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                    ) {

                        composable(GoalTrackerDestinations.Splash.route) {
                            AppSplashScreen(
                                onNavigateToNext = {
                                    if (isOnboardingCompleted) {
                                        navController.navigate(GoalTrackerDestinations.Home.route) {
                                            popUpTo(GoalTrackerDestinations.Splash.route) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(GoalTrackerDestinations.Onboarding.route) {
                                            popUpTo(GoalTrackerDestinations.Splash.route) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                        composable(GoalTrackerDestinations.Onboarding.route) {
                            OnboardingScreen(
                                onFinished = {
                                    mainViewModel.completeOnboarding()
                                    navController.navigate(GoalTrackerDestinations.Home.route) {
                                        popUpTo(GoalTrackerDestinations.Onboarding.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(GoalTrackerDestinations.Home.route) {
                            HomeScreen(navController = navController)
                        }

                        // 2. HABIT DETAIL
                        composable(
                            route = GoalTrackerDestinations.HabitDetail.route,
                            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            HabitDetailScreen(navController)
                        }

                        // 3. İSTATİSTİKLER
                        composable(route = GoalTrackerDestinations.Stats.route) {
                            AnalysisScreen()
                        }

                        // 4. HEDEFLER EKRANI (GOALS)
                        composable(route = GoalTrackerDestinations.Goals.route) {
                            GoalsScreen(
                                onGoalClick = { goalId ->
                                    navController.navigate(GoalTrackerDestinations.GoalDetail.createRoute(goalId))
                                },
                                onChallengeClick = { challengeId ->
                                    navController.navigate(GoalTrackerDestinations.ChallengeDetail.createRoute(challengeId))
                                }
                            )
                        }

                        // 5. HEDEF DETAYI
                        composable(
                            route = GoalTrackerDestinations.GoalDetail.route,
                            arguments = listOf(navArgument("goalId") { type = NavType.IntType })
                        ) {
                            GoalDetailScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 6. MÜCADELELER LİSTESİ
                        composable(GoalTrackerDestinations.Challenges.route) {
                            ChallengeScreen(onNavigateToDetail = { challengeId ->
                                navController.navigate(
                                    GoalTrackerDestinations.ChallengeDetail.createRoute(challengeId)
                                )
                            })
                        }

                        composable(
                            route = GoalTrackerDestinations.ChallengeDetail.route,
                            arguments = listOf(navArgument("challengeId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            ChallengeDetailScreen(
                                onBack = { navController.popBackStack() },
                            )
                        }

                        // 8. AYARLAR
                        composable(GoalTrackerDestinations.Settings.route) {
                             SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}