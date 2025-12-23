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
import com.example.swimpal.viewmodel.UserProfileViewModel

@Composable
fun GeneratedTrainingScreen(
    trainingViewModel: TrainingViewModel = viewModel(),
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    var selectedType by remember { mutableStateOf("Sprinty") }
    var selectedDifficulty by remember { mutableStateOf(1) }
    var selectedDays by remember { mutableStateOf(3) }
    var errorMsg by remember { mutableStateOf("") }
    var infoMsg by remember { mutableStateOf("") }

    val trainingTypes = listOf("Sprinty", "Triathlon", "Open Water", "Technika")
    val badgeState by userProfileViewModel.badgeState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Generuj Trening",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Wybierz parametry treningu",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🏊 Typ treningu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        trainingTypes.take(2).forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        trainingTypes.drop(2).forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💪 Poziom trudności", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = selectedDifficulty.toFloat(),
                    onValueChange = { selectedDifficulty = it.toInt() },
                    valueRange = 1f..3f,
                    steps = 1
                )
                Text("Poziom: $selectedDifficulty ${when(selectedDifficulty) { 1 -> "⭐" 2 -> "⭐⭐" else -> "⭐⭐⭐" }}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📅 Ilość dni treningowych", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = selectedDays.toFloat(),
                    onValueChange = { selectedDays = it.toInt() },
                    valueRange = 1f..6f,
                    steps = 4
                )
                Text("Wybrano: $selectedDays dni")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (infoMsg.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text(infoMsg, modifier = Modifier.padding(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (errorMsg.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(errorMsg, modifier = Modifier.padding(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                infoMsg = ""
                errorMsg = ""
                trainingViewModel.generateAndSaveTraining(
                    type = selectedType,
                    difficulty = selectedDifficulty,
                    days = selectedDays,
                    onSuccess = {
                        infoMsg = "Wygenerowano i zapisano trening!"
                        userProfileViewModel.loadUserProfile()
                    },
                    onError = { errorMsg = it.message ?: "Błąd zapisu treningu" }
                )
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("🚀 Generuj trening", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (badgeState.showDialog) {
        AlertDialog(
            onDismissRequest = { userProfileViewModel.markBadgeAsSeen(badgeState.badgeName) },
            confirmButton = {
                Button(onClick = { userProfileViewModel.markBadgeAsSeen(badgeState.badgeName) }) {
                    Text("OK")
                }
            },
            title = { Text("🎉 Gratulacje!") },
            text = { Text("Zdobywasz nową odznakę:\n\n${badgeState.badgeName}\n${badgeState.badgeDescription}") }
        )
    }
}
