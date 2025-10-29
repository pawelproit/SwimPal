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

@Composable
fun TrainingScreen(
    trainingViewModel: TrainingViewModel = viewModel()
) {
    val customTrainings by trainingViewModel.customTrainings.collectAsState()
    val generatedTrainings by trainingViewModel.generatedTrainings.collectAsState()
    var errorMsg by remember { mutableStateOf("") }
    val expandedDays = remember { mutableStateMapOf<String, Boolean>() }
    var opisExpanded by remember { mutableStateOf(false) }

    // ZMIENNE DLA DIALOGU
    var showDeleteDialog by remember { mutableStateOf(false) }
    var trainingToDelete by remember { mutableStateOf<Pair<String, String>?>(null) }
    // Pair<trainingId, collectionName>

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
                .clickable { opisExpanded = !opisExpanded }
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Opis zadań",
                    style = MaterialTheme.typography.titleMedium
                )
                AnimatedVisibility(
                    visible = opisExpanded,
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Box {
                        Column(modifier = Modifier.padding(12.dp)) {
                            val date = formatDate(training.creationDate)
                            Text(
                                "${training.name}" + if(date.isNotBlank()) ", $date" else "",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            training.days.forEachIndexed { idx, day ->
                                val key = "${training.name}_${day.dayName}"
                                val expanded = expandedDays[key] ?: false
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedDays[key] = !expanded }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Dzień ${idx + 1}",
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
                        IconButton(
                            onClick = {
                                trainingToDelete = Pair(training.id, "custom_trainings")
                                showDeleteDialog = true
                            },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń trening")
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
                    Box {
                        Column(modifier = Modifier.padding(12.dp)) {
                            val date = formatDate(training.creationDate)
                            Text(
                                "${training.name}" + if(date.isNotBlank()) ", $date" else "",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            training.days.forEachIndexed { idx, day ->
                                val key = "${training.name}_${day.dayName}"
                                val expanded = expandedDays[key] ?: false
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedDays[key] = !expanded }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Dzień ${idx + 1}",
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
                        IconButton(
                            onClick = {
                                trainingToDelete = Pair(training.id, "generated_trainings")
                                showDeleteDialog = true
                            },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń trening")
                        }
                    }
                }
            }
        }

        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
    }

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
}
