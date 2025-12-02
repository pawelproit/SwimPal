package com.example.swimpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun GenerateScreen() {
    val navController = rememberNavController()

    val tabs = listOf(
        "generate/generated" to "🤖 Generuj",
        "generate/custom" to "✍️ Napisz"
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

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
