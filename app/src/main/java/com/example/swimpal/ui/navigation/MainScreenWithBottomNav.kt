package com.example.swimpal.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.swimpal.ui.screens.MainScreen
import com.example.swimpal.ui.screens.ProfileScreen
import com.example.swimpal.ui.screens.TrainingScreen
import com.example.swimpal.ui.screens.GenerateScreen

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
                        label = { Text(stringResource(id = item.titleRes)) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
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
                    onNavigateToGenerate = { route ->
                        val generateRoute = if (route == "custom") "generate/custom" else "generate"
                        navController.navigate(generateRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    onNavigateToTraining = {
                        navController.navigate("training") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                    onNavigateToHistory = {
                        navController.navigate("profile") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                        }
                    }
                )
            }

            composable("generate") {
                GenerateScreen(null)
            }

            composable("generate/custom") {
                GenerateScreen("custom")
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
