package com.example.swimpal.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
        "generate/generated" to "Generuj Trening",
        "generate/custom" to "Napisz Trening",
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Column {
        TabRow(
            selectedTabIndex = tabs.indexOfFirst { it.first == currentRoute }.coerceAtLeast(0)
        ) {
            tabs.forEachIndexed { index, pair ->
                Tab(
                    selected = currentRoute == pair.first,
                    onClick = { navController.navigate(pair.first) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    } },
                    text = { Text(pair.second) }
                )
            }
        }

        NavHost(
            navController = navController,
            startDestination = "generate/generated",
            modifier = Modifier.padding(16.dp)
        ) {
            composable("generate/generated") { GeneratedTrainingScreen() }
            composable("generate/custom") { CustomTrainingScreen() }
        }
    }
}
