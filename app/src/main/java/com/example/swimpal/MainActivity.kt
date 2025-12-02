package com.example.swimpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.swimpal.ui.navigation.AppNavGraph
import com.example.swimpal.ui.theme.SwimPalTheme
import com.example.swimpal.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwimPalTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
