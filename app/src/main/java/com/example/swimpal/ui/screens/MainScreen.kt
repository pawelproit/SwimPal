package com.example.swimpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Witaj w aplikacji!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
    }
}
