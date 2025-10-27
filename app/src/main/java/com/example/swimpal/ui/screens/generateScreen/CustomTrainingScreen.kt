package com.example.swimpal.ui.screens.generateScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.model.TrainingTask
import com.example.swimpal.model.TrainingDay
import com.example.swimpal.viewmodel.TrainingViewModel
import androidx.compose.ui.Alignment
import com.example.swimpal.model.TrainingDayInput
import com.example.swimpal.model.TrainingTaskInput

@Composable
fun CustomTrainingScreen(
    trainingViewModel: TrainingViewModel = viewModel(),
    onTrainingSaved: () -> Unit = {}
) {
    var trainingName by remember { mutableStateOf("") }
    var days by remember { mutableStateOf(listOf(
        TrainingDayInput("Dzień 1", listOf(TrainingTaskInput("", "")))
    )) }
    var errorMsg by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Dodaj własny plan treningowy", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = trainingName,
                onValueChange = { trainingName = it },
                label = { Text("Nazwa treningu") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            days.forEachIndexed { dayIdx, dayInput ->
                Card(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = dayInput.dayName,
                            onValueChange = { name ->
                                days = days.mapIndexed { i, d ->
                                    if (i == dayIdx) d.copy(dayName = name) else d
                                }
                            },
                            label = { Text("Nazwa dnia") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        dayInput.tasks.forEachIndexed { taskIdx, taskInput ->
                            OutlinedTextField(
                                value = taskInput.name,
                                onValueChange = { value ->
                                    days = days.mapIndexed { i, d ->
                                        if (i == dayIdx)
                                            d.copy(tasks = d.tasks.mapIndexed { j, t ->
                                                if (j == taskIdx) t.copy(name = value) else t
                                            })
                                        else d
                                    }
                                },
                                label = { Text("Nazwa zadania ${taskIdx + 1}") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = taskInput.description,
                                onValueChange = { value ->
                                    days = days.mapIndexed { i, d ->
                                        if (i == dayIdx)
                                            d.copy(tasks = d.tasks.mapIndexed { j, t ->
                                                if (j == taskIdx) t.copy(description = value) else t
                                            })
                                        else d
                                    }
                                },
                                label = { Text("Opis zadania ${taskIdx + 1}") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                            Row {
                                if (dayInput.tasks.size > 1) {
                                    TextButton(onClick = {
                                        days = days.mapIndexed { i, d ->
                                            if (i == dayIdx)
                                                d.copy(tasks = d.tasks.filterIndexed { j, _ -> j != taskIdx })
                                            else d
                                        }
                                    }) {
                                        Text("Usuń zadanie")
                                    }
                                }
                            }
                        }
                        OutlinedButton(onClick = {
                            days = days.mapIndexed { i, d ->
                                if (i == dayIdx)
                                    d.copy(tasks = d.tasks + TrainingTaskInput("", ""))
                                else d
                            }
                        }) {
                            Text("Dodaj zadanie")
                        }
                        if (days.size > 1) {
                            TextButton(onClick = {
                                days = days.filterIndexed { i, _ -> i != dayIdx }
                            }) {
                                Text("Usuń dzień")
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    days = days + TrainingDayInput(
                        dayName = "Dzień ${days.size + 1}",
                        tasks = listOf(TrainingTaskInput("", ""))
                    )
                }
            ) {
                Text("Dodaj dzień")
            }

            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(76.dp))
        }
        Button(
            onClick = {
                if (trainingName.isBlank() || days.any { it.dayName.isBlank() || it.tasks.any { t -> t.name.isBlank() || t.description.isBlank() } }) {
                    errorMsg = "Wszystkie pola muszą być wypełnione"
                } else {
                    val daysModel = days.map { dayInput ->
                        TrainingDay(
                            dayName = dayInput.dayName,
                            tasks = dayInput.tasks.mapIndexed { idx, t ->
                                TrainingTask(
                                    name = t.name,
                                    description = t.description,
                                    order = idx + 1
                                )
                            }
                        )
                    }
                    trainingViewModel.saveCustomTraining(
                        trainingName,
                        daysModel,
                        onSuccess = {
                            errorMsg = ""
                            onTrainingSaved()
                        },
                        onError = { errorMsg = it.message ?: "Błąd zapisu" }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Utwórz i zapisz trening")
        }
    }
}
