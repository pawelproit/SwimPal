package com.example.swimpal.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color

/**
 * Greeting section showing user's first name and a motivational quote.
 *
 * @param userName Optional first name of the user.
 */

@Composable
fun GreetingSection(userName: String?) {
    Column {
        Text(
            text = "👋 Cześć${if (!userName.isNullOrBlank()) ", $userName" else ""}!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "\"Każdy trening zbliża Cię do celu\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
