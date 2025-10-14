package com.example.swimpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.swimpal.ui.navigation.AppNavGraph
import com.example.swimpal.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()

            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}
