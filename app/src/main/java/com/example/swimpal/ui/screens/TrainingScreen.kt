package com.example.swimpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.TrainingViewModel

@Composable
fun TrainingScreen(
    trainingViewModel: TrainingViewModel = viewModel()
) {
    val trainings by trainingViewModel.trainings.collectAsState()
    var errorMsg by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Text("Ekran Treningów", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        if (trainings.isEmpty()) {
            Text("Brak zapisanych treningów")
        } else {
            trainings.forEach { training ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = training.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        training.tasks.forEachIndexed { i, task ->
                            Text("Zadanie ${i + 1}: ${task.name}", style = MaterialTheme.typography.labelSmall)
                            Text("Opis: ${task.description}", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
    }
}
