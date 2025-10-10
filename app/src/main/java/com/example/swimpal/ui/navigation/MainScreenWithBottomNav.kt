package com.example.swimpal.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.example.swimpal.ui.navigation.BottomNavItem
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
        BottomNavItem.Training,
        BottomNavItem.Generate,
        BottomNavItem.Profile
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(id = item.titleRes)) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
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
            startDestination = BottomNavItem.Main.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Main.route) {
                MainScreen(onLogout = onLogout)
            }
            composable(BottomNavItem.Training.route) {
                TrainingScreen()
            }
            composable(BottomNavItem.Generate.route) {
                GenerateScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
