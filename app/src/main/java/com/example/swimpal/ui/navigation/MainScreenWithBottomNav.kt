package com.example.swimpal.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.swimpal.ui.screens.*
import com.example.swimpal.ui.screens.main.MainScreen

/**
 * Main screen wrapper that provides a bottom navigation bar and nested nav graph.
 *
 * Hosts the main dashboard, generation, training list and profile screens,
 * and manages navigation between them while preserving state where needed.
 *
 * @param onLogout Callback invoked when the user chooses to log out from the profile screen.
 */
@Composable
fun MainScreenWithBottomNav(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem.Main,
        BottomNavItem.Generate,
        BottomNavItem.Training,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.titleRes)) },
                        selected = currentRoute == item.route ||
                                (item.route == "generate" && currentRoute?.startsWith("generate") == true),
                        onClick = {
                            if (item.route == "main" || item.route == "generate") {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id)
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            } else if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(paddingValues)
        ) {

            composable("main") {
                MainScreen(
                    onNavigateToGenerate = { sub ->
                        navController.navigate(
                            if (sub == "custom") "generate/custom" else "generate"
                        ) {
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                    onNavigateToTraining = {
                        navController.navigate("training")
                    },
                    onNavigateToHistory = {
                        navController.navigate("profile")
                    }
                )
            }

            composable("generate") {
                GenerateScreen()
            }

            composable("generate/custom") {
                GenerateScreen(startTab = "custom")
            }

            composable("training") {
                TrainingScreen()
            }

            composable("profile") {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}
