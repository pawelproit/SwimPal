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
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import java.text.SimpleDateFormat
import java.util.*
import com.example.swimpal.model.Training

@Composable
fun TrainingScreen(
    trainingViewModel: TrainingViewModel = viewModel()
) {
    val customTrainings by trainingViewModel.customTrainings.collectAsState()
    val generatedTrainings by trainingViewModel.generatedTrainings.collectAsState()
    var errorMsg by remember { mutableStateOf("") }

    val expandedTrainings = remember { mutableStateMapOf<String, Boolean>() }
    val expandedDays = remember { mutableStateMapOf<String, Boolean>() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var trainingToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }

    var showCompleteDialog by remember { mutableStateOf(false) }
    var trainingToComplete by remember { mutableStateOf<Pair<Training, String>?>(null) }
    var completeRating by remember { mutableStateOf(3) }
    var completeNote by remember { mutableStateOf("") }
    var isCompleting by remember { mutableStateOf(false) }

    val globalOpis = "Opis zadań: Tutaj znajdziesz wyjaśnienia techniczne, wskazówki i dodatkowe informacje dotyczące wszystkich ćwiczeń, które pojawiają się w treningach. Skup się na technice, oddychaniu, tempie pływania i regeneracji między kolejnymi zadaniami."

    fun formatDate(dateString: String): String {
        return try {
            if (dateString.isBlank()) return ""
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = input.parse(dateString)
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date ?: return dateString)
        } catch (e: Exception) {
            dateString
        }
    }

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
                Text(
                    text = "Opis zadań",
                    style = MaterialTheme.typography.titleMedium
                )
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

        Text("Treningi własne", style = MaterialTheme.typography.titleMedium)
        if (customTrainings.isEmpty()) {
            Text("Brak własnych treningów")
        } else {
            customTrainings.forEach { training ->
                val expanded = expandedTrainings[training.id] ?: false
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedTrainings[training.id] = !expanded }
                                .padding(end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val date = formatDate(training.creationDate)
                            Text(
                                text = "${training.name}" + if(date.isNotBlank()) ", $date" else "",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    trainingToDelete = Pair(training.id, "custom_trainings")
                                    showDeleteDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń trening")
                            }
                        }
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column {
                                training.days.forEachIndexed { idx, day ->
                                    val dayKey = "${training.id}_$idx"
                                    val expandedDay = expandedDays[dayKey] ?: false
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expandedDays[dayKey] = !expandedDay }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Dzień ${idx + 1}",
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = if (expandedDay) "▲" else "▼",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                    AnimatedVisibility(
                                        visible = expandedDay,
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        Column(modifier = Modifier.padding(start = 16.dp)) {
                                            day.tasks.sortedBy { it.order }.forEach { task ->
                                                Text("${task.order}. ${task.name}", style = MaterialTheme.typography.labelSmall)
                                                Text("Opis: ${task.description}", style = MaterialTheme.typography.bodySmall)
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }
                                    }
                                }
                                // Przycisk "Oznacz jako wykonane" pod ostatnim dniem
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        trainingToComplete = Pair(training, "custom_trainings")
                                        completeRating = 3
                                        completeNote = ""
                                        showCompleteDialog = true
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Oznacz jako wykonane")
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
                val expanded = expandedTrainings[training.id] ?: false
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedTrainings[training.id] = !expanded }
                                .padding(end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val date = formatDate(training.creationDate)
                            Text(
                                text = "${training.name}" + if(date.isNotBlank()) ", $date" else "",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    trainingToDelete = Pair(training.id, "generated_trainings")
                                    showDeleteDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń trening")
                            }
                        }
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column {
                                training.days.forEachIndexed { idx, day ->
                                    val dayKey = "${training.id}_$idx"
                                    val expandedDay = expandedDays[dayKey] ?: false
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expandedDays[dayKey] = !expandedDay }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Dzień ${idx + 1}",
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = if (expandedDay) "▲" else "▼",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                    AnimatedVisibility(
                                        visible = expandedDay,
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        Column(modifier = Modifier.padding(start = 16.dp)) {
                                            day.tasks.sortedBy { it.order }.forEach { task ->
                                                Text("${task.order}. ${task.name}", style = MaterialTheme.typography.labelSmall)
                                                Text("Opis: ${task.description}", style = MaterialTheme.typography.bodySmall)
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        trainingToComplete = Pair(training, "generated_trainings")
                                        completeRating = 3
                                        completeNote = ""
                                        showCompleteDialog = true
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Oznacz jako wykonane")
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

    // --- Dialog do usunięcia treningu ---
    if (showDeleteDialog && trainingToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        trainingViewModel.deleteTraining(trainingToDelete!!.first, trainingToDelete!!.second,
                            onSuccess = { showDeleteDialog = false },
                            onError = {
                                errorMsg = "Błąd podczas usuwania treningu"
                                showDeleteDialog = false
                            }
                        )
                    }
                ) { Text("Tak") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) { Text("Nie") }
            },
            title = { Text("Usuń trening") },
            text = { Text("Czy na pewno chcesz usunąć ten trening?") }
        )
    }

    // --- Dialog "Oznacz jako wykonane"/ocena/notatka/zapisz ---
    if (showCompleteDialog && trainingToComplete != null) {
        AlertDialog(
            onDismissRequest = { if (!isCompleting) showCompleteDialog = false },
            title = { Text("Ocena treningu") },
            text = {
                Column {
                    Text("Jak oceniasz ten trening?")
                    Row(
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    ) {
                        (1..5).forEach { value ->
                            Button(
                                onClick = { completeRating = value },
                                colors = if (completeRating == value) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text("$value")
                            }
                        }
                    }
                    OutlinedTextField(
                        value = completeNote,
                        onValueChange = { completeNote = it },
                        label = { Text("Notatka (opcjonalnie)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isCompleting
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isCompleting = true
                        val (training, collection) = trainingToComplete!!
                        trainingViewModel.completeTraining(
                            training, completeRating, completeNote,
                            onSuccess = {
                                isCompleting = false
                                showCompleteDialog = false
                            },
                            onError = {
                                errorMsg = "Błąd przy zapisie oceny/notatki"
                                isCompleting = false
                                showCompleteDialog = false
                            }
                        )
                    },
                    enabled = !isCompleting
                ) { Text("Zapisz") }
            },
            dismissButton = {
                TextButton(
                    onClick = { if (!isCompleting) showCompleteDialog = false },
                    enabled = !isCompleting
                ) { Text("Anuluj") }
            }
        )
    }
}
