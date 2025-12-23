package com.example.swimpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.swimpal.ui.navigation.AppNavGraph
import com.example.swimpal.ui.theme.SwimPalTheme
import com.example.swimpal.viewmodel.AuthViewModel

/**
 * Main entry point of the SwimPal application.
 *
 * This activity is responsible for:
 * - Initializing Jetpack Compose.
 * - Applying a global application theme.
 * - Enforcing a fixed font scale across the app.
 * - Creating and providing the main navigation controller.
 * - Providing the shared [AuthViewModel] used for authentication flow.
 *
 * All UI rendering and navigation logic is delegated to [AppNavGraph].
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is first created.
     *
     * Sets up the Compose content hierarchy and global UI configuration.
     *
     * - Enables edge-to-edge rendering.
     * - Overrides system font scaling to ensure consistent UI appearance.
     * - Installs the app theme.
     * - Initializes navigation and shared ViewModels.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(
                /**
                 * Forces a fixed font scale (1.0) regardless of system settings.
                 * This ensures predictable UI layout and typography.
                 */
                LocalDensity provides Density(
                    density = LocalDensity.current.density,
                    fontScale = 1f
                )
            ) {
                /**
                 * Applies the SwimPal Material theme to the entire app.
                 */
                SwimPalTheme {

                    /**
                     * Main navigation controller used across the app.
                     */
                    val navController = rememberNavController()

                    /**
                     * Shared authentication ViewModel.
                     * Lives as long as the activity and is accessible across screens.
                     */
                    val authViewModel: AuthViewModel = viewModel()

                    /**
                     * Hosts the application's navigation graph and screen hierarchy.
                     */
                    AppNavGraph(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}
