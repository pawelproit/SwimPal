package com.example.swimpal.ui.screens.generateScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.TrainingViewModel
import com.example.swimpal.viewmodel.UserProfileViewModel
import com.example.swimpal.viewmodel.ProfileState

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

    var showBadgeDialog by remember { mutableStateOf(false) }
    var newBadge by remember { mutableStateOf<Pair<String, String>?>(null) }
    val profileState by userProfileViewModel.profileState.collectAsState()

    LaunchedEffect(profileState) {
        if (profileState is ProfileState.Success) {
            val profile = (profileState as ProfileState.Success).userProfile
            val newlyAchievedBadge = profile.badges.firstOrNull { it.achieved && it.isNew }

            if (newlyAchievedBadge != null) {
                newBadge = newlyAchievedBadge.name to newlyAchievedBadge.description
                showBadgeDialog = true
            }
        }
    }

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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🏊 Typ treningu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        trainingTypes.take(2).forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
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
                                label = { Text(type) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💪 Poziom trudności",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = selectedDifficulty.toFloat(),
                    onValueChange = { selectedDifficulty = it.toInt() },
                    valueRange = 1f..3f,
                    steps = 1,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Poziom: $selectedDifficulty ${when(selectedDifficulty) { 1 -> "⭐" 2 -> "⭐⭐" else -> "⭐⭐⭐" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📅 Ilość dni treningowych",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = selectedDays.toFloat(),
                    onValueChange = { selectedDays = it.toInt() },
                    valueRange = 1f..6f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Wybrano: $selectedDays dni",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (infoMsg.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = infoMsg,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (errorMsg.isNotEmpty()) {
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
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "🚀 Generuj trening",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showBadgeDialog && newBadge != null) {
        AlertDialog(
            onDismissRequest = {
                showBadgeDialog = false
                userProfileViewModel.markBadgeAsSeen(newBadge!!.first)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBadgeDialog = false
                        userProfileViewModel.markBadgeAsSeen(newBadge!!.first)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("OK")
                }
            },
            title = { Text("🎉 Gratulacje!") },
            text = { Text("Zdobywasz nową odznakę:\n\n${newBadge!!.first}\n${newBadge!!.second}") },
            containerColor = Color.White
        )
    }
}
