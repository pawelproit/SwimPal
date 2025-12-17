package com.example.swimpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.swimpal.ui.screens.generateScreen.GeneratedTrainingScreen
import com.example.swimpal.ui.screens.generateScreen.CustomTrainingScreen

@Composable
fun GenerateScreen(subroute: String? = null) {
    val navController = rememberNavController()

    val tabs = listOf(
        "generate/generated" to "🤖 Generuj",
        "generate/custom" to "✍️ Napisz"
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    LaunchedEffect(subroute) {
        val targetRoute = when (subroute) {
            "custom" -> "generate/custom"
            else -> "generate/generated"
        }
        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE1F5FE),
                        Color(0xFFF0F8FF)
                    )
                )
            )
    ) {
        Column {
            TabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == currentRoute }.coerceAtLeast(0),
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, pair ->
                    Tab(
                        selected = currentRoute == pair.first,
                        onClick = {
                            navController.navigate(pair.first) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        text = { Text(pair.second) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            NavHost(
                navController = navController,
                startDestination = "generate/generated",
                modifier = Modifier.padding(0.dp)
            ) {
                composable("generate/generated") { GeneratedTrainingScreen() }
                composable("generate/custom") { CustomTrainingScreen() }
            }
        }
    }
}
