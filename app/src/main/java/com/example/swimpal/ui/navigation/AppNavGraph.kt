package com.example.swimpal.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.swimpal.ui.screens.LoginScreen
import com.example.swimpal.ui.screens.RegisterScreen
import com.example.swimpal.ui.screens.PersonalDataScreen
import com.example.swimpal.ui.screens.WelcomeScreen
import com.example.swimpal.viewmodel.AuthState
import com.example.swimpal.viewmodel.AuthViewModel
import com.example.swimpal.viewmodel.UserProfileViewModel

/**
 * Root navigation graph for the application.
 *
 * Handles navigation between the welcome screen, authentication flow
 * (login/register), personal data form and the main screen with bottom
 * navigation. Listens to [AuthState] and redirects after successful login
 * or registration.
 *
 * @param navController Controller used to perform navigation actions.
 * @param authViewModel ViewModel responsible for authentication state.
 * @param userProfileViewModel ViewModel responsible for user profile operations.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val profileState by userProfileViewModel.profileState.collectAsState()

    val startDestination = "welcome"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("welcome") {
            WelcomeScreen(
                onContinue = {
                    if (authViewModel.isUserLoggedIn()) {
                        navController.navigate("main") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                authState = authState,
                onLogin = { email, password -> authViewModel.login(email, password) },
                onNavigateToRegister = { navController.navigate("register") }
            )
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }

        composable("register") {
            RegisterScreen(
                authState = authState,
                onRegister = { email, password ->
                    authViewModel.register(email, password)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    navController.navigate("personalData") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            }
        }

        composable("personalData") {
            PersonalDataScreen(
                profileState = profileState,
                onSave = { userProfile ->
                    userProfileViewModel.saveUserProfile(userProfile)
                },
                onSuccess = {
                    navController.navigate("main") {
                        popUpTo("personalData") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreenWithBottomNav(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
