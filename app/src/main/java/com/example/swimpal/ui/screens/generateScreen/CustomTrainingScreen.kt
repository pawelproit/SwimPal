package com.example.swimpal.ui.screens.generateScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.model.TrainingTask
import com.example.swimpal.model.TrainingDay
import com.example.swimpal.model.TrainingDayInput
import com.example.swimpal.model.TrainingTaskInput
import com.example.swimpal.viewmodel.TrainingViewModel
import com.example.swimpal.viewmodel.UserProfileViewModel

@Composable
fun CustomTrainingScreen(
    trainingViewModel: TrainingViewModel = viewModel(),
    userProfileViewModel: UserProfileViewModel = viewModel(),
    onTrainingSaved: () -> Unit = {}
) {
    var trainingName by remember { mutableStateOf("") }
    var days by remember {
        mutableStateOf(listOf(TrainingDayInput("Dzień 1", listOf(TrainingTaskInput("", "")))))
    }
    var errorMsg by remember { mutableStateOf("") }
    var infoMsg by remember { mutableStateOf("") }

    val badgeState by userProfileViewModel.badgeState

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
                Card(modifier = Modifier.padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = dayInput.dayName,
                            onValueChange = { name ->
                                days = days.toMutableList().apply {
                                    this[dayIdx] = this[dayIdx].copy(dayName = name)
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
                                    days = days.toMutableList().apply {
                                        this[dayIdx] = this[dayIdx].copy(
                                            tasks = this[dayIdx].tasks.toMutableList().apply {
                                                this[taskIdx] = this[taskIdx].copy(name = value)
                                            }
                                        )
                                    }
                                },
                                label = { Text("Nazwa zadania ${taskIdx + 1}") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = taskInput.description,
                                onValueChange = { value ->
                                    days = days.toMutableList().apply {
                                        this[dayIdx] = this[dayIdx].copy(
                                            tasks = this[dayIdx].tasks.toMutableList().apply {
                                                this[taskIdx] = this[taskIdx].copy(description = value)
                                            }
                                        )
                                    }
                                },
                                label = { Text("Opis zadania ${taskIdx + 1}") },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            )
                            if (dayInput.tasks.size > 1) {
                                Row {
                                    TextButton(onClick = {
                                        days = days.toMutableList().apply {
                                            this[dayIdx] = this[dayIdx].copy(
                                                tasks = this[dayIdx].tasks.filterIndexed { j, _ -> j != taskIdx }
                                            )
                                        }
                                    }) {
                                        Text("Usuń zadanie")
                                    }
                                }
                            }
                        }
                        OutlinedButton(onClick = {
                            days = days.toMutableList().apply {
                                this[dayIdx] = this[dayIdx].copy(
                                    tasks = this[dayIdx].tasks + TrainingTaskInput("", "")
                                )
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

            Spacer(modifier = Modifier.height(16.dp))

            if (infoMsg.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        infoMsg,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorMsg.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        errorMsg,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(76.dp))
        }

        Button(
            onClick = {
                errorMsg = ""
                infoMsg = ""
                if (trainingName.isBlank() || days.any {
                        it.dayName.isBlank() || it.tasks.any { t -> t.name.isBlank() || t.description.isBlank() }
                    }) {
                    errorMsg = "Wszystkie pola muszą być wypełnione"
                } else {
                    val daysModel = days.map { dayInput ->
                        TrainingDay(
                            dayName = dayInput.dayName,
                            tasks = dayInput.tasks.mapIndexed { idx, t ->
                                TrainingTask(name = t.name, description = t.description, order = idx + 1)
                            }
                        )
                    }
                    trainingViewModel.saveCustomTraining(
                        trainingName,
                        daysModel,
                        onSuccess = {
                            infoMsg = "Stworzono i zapisano trening!"
                            trainingName = ""
                            days = listOf(TrainingDayInput("Dzień 1", listOf(TrainingTaskInput("", ""))))
                            userProfileViewModel.loadUserProfile()
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
