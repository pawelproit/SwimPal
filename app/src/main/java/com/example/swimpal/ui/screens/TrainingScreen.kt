package com.example.swimpal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.TrainingViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun TrainingScreen(
    trainingViewModel: TrainingViewModel = viewModel()
) {
    val customTrainings by trainingViewModel.customTrainings.collectAsState()
    val generatedTrainings by trainingViewModel.generatedTrainings.collectAsState()
    var errorMsg by remember { mutableStateOf("") }

    val expandedDays = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Ekran Treningów", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Treningi własne", style = MaterialTheme.typography.titleMedium)
        if (customTrainings.isEmpty()) {
            Text("Brak własnych treningów")
        } else {
            customTrainings.forEach { training ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(training.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        training.days.forEach { day ->
                            val key = "${training.name}_${day.dayName}"
                            val expanded = expandedDays[key] ?: false
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedDays[key] = !expanded }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = day.dayName,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                AnimatedVisibility(
                                    visible = expanded,
                                    enter = expandVertically(),
                                    exit = shrinkVertically()
                                ) {
                                    Column {
                                        day.tasks.sortedBy { it.order }.forEach { task ->
                                            Text("${task.order}. ${task.name}", style = MaterialTheme.typography.labelSmall)
                                            Text("Opis: ${task.description}", style = MaterialTheme.typography.bodySmall)
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Treningi generowane", style = MaterialTheme.typography.titleMedium)
        if (generatedTrainings.isEmpty()) {
            Text("Brak generowanych treningów")
        } else {
            generatedTrainings.forEach { training ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(training.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        training.days.forEach { day ->
                            val key = "${training.name}_${day.dayName}"
                            val expanded = expandedDays[key] ?: false
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedDays[key] = !expanded }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = day.dayName,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                AnimatedVisibility(
                                    visible = expanded,
                                    enter = expandVertically(),
                                    exit = shrinkVertically()
                                ) {
                                    Column {
                                        day.tasks.sortedBy { it.order }.forEach { task ->
                                            Text("${task.order}. ${task.name}", style = MaterialTheme.typography.labelSmall)
                                            Text("Opis: ${task.description}", style = MaterialTheme.typography.bodySmall)
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }
                                    }
                                }
                            }
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
