package com.example.swimpal.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.swimpal.ui.screens.ProfileScreen
import com.example.swimpal.ui.screens.TrainingScreen
import com.example.swimpal.ui.screens.MainScreen
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
                    onNavigateToGenerate = {
                        navController.navigate("generate") {
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
            composable("training") {
                TrainingScreen()
            }
            composable("generate") {
                GenerateScreen()
            }
            composable("profile") {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}
