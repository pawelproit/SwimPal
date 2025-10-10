package com.example.swimpal


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.ui.navigation.MainScreenWithBottomNav
import com.example.swimpal.ui.screens.LoginScreen
import com.example.swimpal.ui.screens.MainScreen
import com.example.swimpal.ui.screens.RegisterScreen
import com.example.swimpal.viewmodel.AuthState
import com.example.swimpal.viewmodel.AuthViewModel
import com.example.swimpal.ui.navigation.MainScreenWithBottomNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val authState by authViewModel.authState.collectAsState()
            var currentScreen by remember { mutableStateOf("login") }

            LaunchedEffect(Unit) {
                if (authViewModel.isUserLoggedIn()) {
                    currentScreen = "main"
                }
            }

            when (currentScreen) {
                "login" -> LoginScreen(
                    authState = authState,
                    onLogin = { email, password -> authViewModel.login(email, password) },
                    onNavigateToRegister = { currentScreen = "register" }
                )

                "register" -> RegisterScreen(
                    authState = authState,
                    onRegister = { email, password -> authViewModel.register(email, password) },
                    onNavigateToLogin = {
                        currentScreen = "login"
                    }
                )

                "main" -> MainScreenWithBottomNav(
                    onLogout = {
                        authViewModel.logout()
                        currentScreen = "login"
                    }
                )
            }

            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    currentScreen = "main"
                }
            }
        }
    }
}
