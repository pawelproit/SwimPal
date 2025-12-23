package com.example.swimpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.*
import com.example.swimpal.ui.screens.generateScreen.*

/**
 * Wrapper composable providing a two-tab interface for training generation.
 *
 * Creates an internal NavController and hosts a nested navigation graph
 * with two routes: generated trainings and a custom training editor.
 * The selected tab is derived from the current navigation route.
 *
 * @param startTab Optional initial tab selector; when equal to "custom",
 * the custom training editor is shown first, otherwise the generated tab is used.
 */

@Composable
fun GenerateScreen(startTab: String? = null) {

    val navController = rememberNavController()

    val startDestination =
        if (startTab == "custom") "generate/custom"
        else "generate/generated"

    val tabs = listOf(
        "generate/generated" to "🤖 Generuj",
        "generate/custom" to "✍️ Napisz"
    )

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE1F5FE), Color(0xFFF0F8FF))
                )
            )
    ) {
        Column {

            TabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == currentRoute }
                    .coerceAtLeast(0)
            ) {
                tabs.forEach { (route, label) ->
                    Tab(
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
                        },
                        text = { Text(label) }
                    )
                }
            }

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("generate/generated") {
                    GeneratedTrainingScreen()
                }
                composable("generate/custom") {
                    CustomTrainingScreen()
                }
            }
        }
    }
}
