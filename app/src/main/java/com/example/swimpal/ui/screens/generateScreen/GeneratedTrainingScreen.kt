package com.example.swimpal.ui.screens.generateScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.TrainingViewModel

@Composable
fun GeneratedTrainingScreen(trainingViewModel: TrainingViewModel = viewModel()) {
    var selectedType by remember { mutableStateOf("Sprinty") }
    var selectedDifficulty by remember { mutableStateOf(1) }
    var selectedDays by remember { mutableStateOf(3) }
    var errorMsg by remember { mutableStateOf("") }
    var infoMsg by remember { mutableStateOf("") }

    val trainingTypes = listOf("Sprinty", "Triathlon", "Open Water", "Technika")

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    infoMsg = ""
                    errorMsg = ""
                    trainingViewModel.generateAndSaveTraining(
                        type = selectedType,
                        difficulty = selectedDifficulty,
                        days = selectedDays,
                        onSuccess = { infoMsg = "Wygenerowano i zapisano trening!" },
                        onError = { errorMsg = it.message ?: "Błąd zapisu treningu" }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Generuj trening")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Generowanie Treningu",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text("Typ treningu:", style = MaterialTheme.typography.titleSmall)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trainingTypes.take(2).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trainingTypes.drop(2).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type) }
                        )
                    }
                }
            }

            Text("Poziom trudności (1–3):", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = selectedDifficulty.toFloat(),
                onValueChange = { selectedDifficulty = it.toInt() },
                valueRange = 1f..3f,
                steps = 1
            )
            Text("Wybrany poziom: $selectedDifficulty")

            Text("Ilość dni treningowych (1–6):", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = selectedDays.toFloat(),
                onValueChange = { selectedDays = it.toInt() },
                valueRange = 1f..6f,
                steps = 4
            )
            Text("Wybrano dni: $selectedDays")

            if (infoMsg.isNotEmpty()) Text(infoMsg, color = MaterialTheme.colorScheme.primary)
            if (errorMsg.isNotEmpty()) Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
    }
}
