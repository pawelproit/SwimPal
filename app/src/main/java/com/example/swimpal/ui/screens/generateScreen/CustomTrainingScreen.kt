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
import com.example.swimpal.viewmodel.TrainingViewModel
import androidx.compose.ui.Alignment


@Composable
fun CustomTrainingScreen(
    trainingViewModel: TrainingViewModel = viewModel(),
    onTrainingSaved: () -> Unit = {}
) {
    var trainingName by remember { mutableStateOf("") }
    var taskNames by remember { mutableStateOf(List(4) { "" }) }
    var taskDescriptions by remember { mutableStateOf(List(4) { "" }) }
    var errorMsg by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Tutaj możesz ręcznie wpisać trening", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = trainingName,
                onValueChange = { trainingName = it },
                label = { Text("Nazwa treningu") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            for (i in 0 until 4) {
                OutlinedTextField(
                    value = taskNames[i],
                    onValueChange = { value ->
                        taskNames = taskNames.toMutableList().also { it[i] = value }
                    },
                    label = { Text("Nazwa zadania ${i + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = taskDescriptions[i],
                    onValueChange = { value ->
                        taskDescriptions = taskDescriptions.toMutableList().also { it[i] = value }
                    },
                    label = { Text("Opis zadania ${i + 1}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(76.dp)) // miejsce na przycisk
        }
        Button(
            onClick = {
                if (trainingName.isBlank() || taskNames.any { it.isBlank() } || taskDescriptions.any { it.isBlank() }) {
                    errorMsg = "Wszystkie pola muszą być wypełnione"
                } else {
                    trainingViewModel.saveCustomTraining(
                        trainingName,
                        List(4) { TrainingTask(taskNames[it], taskDescriptions[it]) },
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
            Text("Utwórz")
        }
    }
}
