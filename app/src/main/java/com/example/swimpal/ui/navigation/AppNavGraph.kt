package com.example.swimpal.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.swimpal.ui.screens.LoginScreen
import com.example.swimpal.ui.screens.RegisterScreen
import com.example.swimpal.viewmodel.AuthState
import com.example.swimpal.viewmodel.AuthViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    val startDestination = if (authViewModel.isUserLoggedIn()) "main" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {
            LoginScreen(
                authState = authState,
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
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
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            }
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
