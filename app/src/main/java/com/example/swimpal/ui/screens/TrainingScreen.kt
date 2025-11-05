package com.example.swimpal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.TrainingViewModel
import com.example.swimpal.model.Training
import com.example.swimpal.ui.components.*
@Composable
fun TrainingScreen(
    trainingViewModel: TrainingViewModel = viewModel()
) {
    val customTrainings by trainingViewModel.customTrainings.collectAsState()
    val generatedTrainings by trainingViewModel.generatedTrainings.collectAsState()
    var errorMsg by remember { mutableStateOf("") }

    val expandedTrainings = remember { mutableStateMapOf<String, Boolean>() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var trainingToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var trainingToComplete by remember { mutableStateOf<Pair<Training, String>?>(null) }
    var completeRating by remember { mutableStateOf(3) }
    var completeNote by remember { mutableStateOf("") }
    var isCompleting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Ekran Treningów", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { expandedTrainings["opis"] = !(expandedTrainings["opis"] ?: false) }
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Opis zadań", style = MaterialTheme.typography.titleMedium)
                AnimatedVisibility(
                    visible = expandedTrainings["opis"] ?: false,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Text(
                        text = globalOpis,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        TrainingSection(
            title = "Treningi własne",
            trainings = customTrainings,
            expandedTrainings = expandedTrainings,
            onDeleteClick = { trainingId ->
                trainingToDelete = Pair(trainingId, "custom_trainings"); showDeleteDialog = true
            },
            onCompleteClick = { training ->
                trainingToComplete = Pair(training, "custom_trainings"); completeRating = 3; completeNote = ""; showCompleteDialog = true
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        TrainingSection(
            title = "Treningi generowane",
            trainings = generatedTrainings,
            expandedTrainings = expandedTrainings,
            onDeleteClick = { trainingId ->
                trainingToDelete = Pair(trainingId, "generated_trainings"); showDeleteDialog = true
            },
            onCompleteClick = { training ->
                trainingToComplete = Pair(training, "generated_trainings"); completeRating = 3; completeNote = ""; showCompleteDialog = true
            }
        )

        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
    }

    TrainingDeleteDialog(
        show = showDeleteDialog && trainingToDelete != null,
        onConfirm = {
            val (trainingId, collection) = trainingToDelete!!
            showDeleteDialog = false; trainingToDelete = null
            trainingViewModel.deleteTraining(trainingId, collection, onSuccess = {}, onError = { e ->
                errorMsg = "Błąd podczas usuwania treningu: ${e.message}"
            })
        },
        onDismiss = { showDeleteDialog = false; trainingToDelete = null }
    )

    TrainingCompleteDialog(
        show = showCompleteDialog && trainingToComplete != null,
        rating = completeRating,
        note = completeNote,
        isCompleting = isCompleting,
        onRatingChange = { completeRating = it },
        onNoteChange = { completeNote = it },
        onConfirm = {
            isCompleting = true
            val (training, collection) = trainingToComplete!!
            trainingViewModel.completeTraining(
                training, collection, completeRating, completeNote,
                onSuccess = {
                    isCompleting = false
                    showCompleteDialog = false
                    trainingToComplete = null
                    completeRating = 3
                    completeNote = ""
                },
                onError = { e ->
                    errorMsg = "Błąd przy zapisie oceny/notatki: ${e.message}"
                    isCompleting = false
                    showCompleteDialog = false
                    trainingToComplete = null
                }
            )
        },
        onDismiss = { if (!isCompleting) { showCompleteDialog = false; trainingToComplete = null } }
    )
}

@Composable
private fun TrainingSection(
    title: String,
    trainings: List<Training>,
    expandedTrainings: MutableMap<String, Boolean>,
    onDeleteClick: (String) -> Unit,
    onCompleteClick: (Training) -> Unit
) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    if (trainings.isEmpty()) {
        Text("Brak ${title.lowercase()}")
    } else {
        trainings.forEach { training ->
            TrainingCard(
                training = training,
                expanded = expandedTrainings[training.id] ?: false,
                onExpandClick = { expandedTrainings[training.id] = !(expandedTrainings[training.id] ?: false) },
                onDeleteClick = { onDeleteClick(training.id) },
                onCompleteClick = { onCompleteClick(training) }
            )
        }
    }
}
