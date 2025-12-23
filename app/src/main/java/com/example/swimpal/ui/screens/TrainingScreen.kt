package com.example.swimpal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.TrainingViewModel
import com.example.swimpal.model.Training
import com.example.swimpal.ui.components.GeneratedFilterDropdown
import com.example.swimpal.ui.components.TrainingCard
import com.example.swimpal.ui.components.TrainingCompleteDialog
import com.example.swimpal.ui.components.TrainingDeleteDialog
import com.example.swimpal.ui.components.globalOpis

/**
 * Main screen for browsing and managing user's training plans in the SwimPal app.
 *
 * Presents generated and custom trainings with filtering, expandable cards,
 * and dialogs for completing or deleting a training. The UI is fully state-driven
 * and uses Material 3 components with subtle animations and a gradient background.
 *
 * All business logic (loading, deleting, completing trainings) is delegated
 * to [TrainingViewModel]; this composable focuses solely on rendering UI state
 * and forwarding user intents.
 *
 * @param trainingViewModel ViewModel providing training data and handling user actions.
 */

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

    var selectedGeneratedFilter by remember { mutableStateOf("Wszystko") }
    val availableFilters = listOf("Wszystko", "Sprinty", "Open Water", "Technika", "Triathlon")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE1F5FE),
                        Color(0xFFF0F8FF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Moje Treningi",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Zarządzaj swoimi planami treningowymi",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { expandedTrainings["opis"] = !(expandedTrainings["opis"] ?: false) },
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📚 Opis zadań",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (expandedTrainings["opis"] == true) "▲" else "▼",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    AnimatedVisibility(
                        visible = expandedTrainings["opis"] ?: false,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Text(
                            text = globalOpis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "🤖 Treningi generowane",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            GeneratedFilterDropdown(
                selected = selectedGeneratedFilter,
                options = availableFilters,
                onSelectedChange = { selectedGeneratedFilter = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            val filteredGenerated = when (selectedGeneratedFilter) {
                "Wszystko" -> generatedTrainings
                else -> generatedTrainings.filter { it.type == selectedGeneratedFilter }
            }

            TrainingSection(
                title = "",
                trainings = filteredGenerated,
                expandedTrainings = expandedTrainings,
                onDeleteClick = { trainingId ->
                    trainingToDelete = Pair(trainingId, "generated_trainings")
                    showDeleteDialog = true
                },
                onCompleteClick = { training ->
                    trainingToComplete = Pair(training, "generated_trainings")
                    completeRating = 3
                    completeNote = ""
                    showCompleteDialog = true
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            TrainingSection(
                title = "💪 Treningi własne",
                trainings = customTrainings,
                expandedTrainings = expandedTrainings,
                onDeleteClick = { trainingId ->
                    trainingToDelete = Pair(trainingId, "custom_trainings")
                    showDeleteDialog = true
                },
                onCompleteClick = { training ->
                    trainingToComplete = Pair(training, "custom_trainings")
                    completeRating = 3
                    completeNote = ""
                    showCompleteDialog = true
                }
            )

            if (errorMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    TrainingDeleteDialog(
        show = showDeleteDialog && trainingToDelete != null,
        onConfirm = {
            val (trainingId, collection) = trainingToDelete!!
            showDeleteDialog = false
            trainingToDelete = null
            trainingViewModel.deleteTraining(
                trainingId,
                collection,
                onSuccess = {},
                onError = { e ->
                    errorMsg = "Błąd podczas usuwania treningu: ${e.message}"
                }
            )
        },
        onDismiss = {
            showDeleteDialog = false
            trainingToDelete = null
        }
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
                training,
                collection,
                completeRating,
                completeNote,
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
        onDismiss = {
            if (!isCompleting) {
                showCompleteDialog = false
                trainingToComplete = null
            }
        }
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
    Column {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (trainings.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📭",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (title.isNotEmpty()) {
                                "Brak ${title.lowercase().replace("💪 ", "").replace("🤖 ", "")}"
                            } else {
                                "Brak treningów"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            trainings.forEach { training ->
                TrainingCard(
                    training = training,
                    expanded = expandedTrainings[training.id] ?: false,
                    onExpandClick = {
                        expandedTrainings[training.id] = !(expandedTrainings[training.id] ?: false)
                    },
                    onDeleteClick = { onDeleteClick(training.id) },
                    onCompleteClick = { onCompleteClick(training) }
                )
            }
        }
    }
}
