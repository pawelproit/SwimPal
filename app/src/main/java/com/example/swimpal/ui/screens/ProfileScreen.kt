package com.example.swimpal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.model.Training
import com.example.swimpal.viewmodel.ProfileState
import com.example.swimpal.viewmodel.UserProfileViewModel
import com.example.swimpal.viewmodel.TrainingViewModel
import com.example.swimpal.ui.components.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    userProfileViewModel: UserProfileViewModel = viewModel(),
    trainingViewModel: TrainingViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val profileState by userProfileViewModel.profileState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var prevBadges by remember { mutableStateOf<List<com.example.swimpal.model.Badge>>(emptyList()) }
    var expandedData by remember { mutableStateOf(false) }
    var expandedBadges by remember { mutableStateOf(false) }
    var expandedVideo by remember { mutableStateOf(false) }
    var expandedHistory by remember { mutableStateOf(false) }

    val historyTrainings by trainingViewModel.historyTrainings.collectAsState(initial = emptyList())
    val expandedHistoryTrainings = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(Unit) {
        userProfileViewModel.loadUserProfile()
    }

    LaunchedEffect(profileState) {
        if (profileState is ProfileState.Success) {
            val profile = (profileState as ProfileState.Success).userProfile
            val current = profile.badges
            val newBadges = current.filterIndexed { i, badge ->
                badge.achieved && (prevBadges.getOrNull(i)?.achieved == false)
            }
            prevBadges = current
            if (newBadges.isNotEmpty()) {
                val b = newBadges.first()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Gratulacje! Zdobyłeś odznakę: \"${b.name}\" - ${b.description}"
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Text("Twój profil", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    ExpandableCard(
                        title = "Dane",
                        expanded = expandedData,
                        onToggle = { expandedData = !expandedData }
                    ) {
                        when (profileState) {
                            is ProfileState.Loading -> Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                            is ProfileState.Success -> {
                                val profile = (profileState as ProfileState.Success).userProfile
                                ProfileUserDataSection(
                                    profile = profile,
                                    onProfileChanged = { userProfileViewModel.saveUserProfile(it) }
                                )
                            }
                            is ProfileState.Error -> Text(
                                "Błąd: ${(profileState as ProfileState.Error).error}",
                                color = MaterialTheme.colorScheme.error
                            )
                            else -> Text("Brak danych profilu.")
                        }
                    }
                }

                item {
                    ExpandableCard(
                        title = "Odznaki",
                        expanded = expandedBadges,
                        onToggle = { expandedBadges = !expandedBadges }
                    ) {
                        when (profileState) {
                            is ProfileState.Success -> {
                                val profile = (profileState as ProfileState.Success).userProfile
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Statystyki", style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row {
                                    Text("Custom: ${profile.customCount}", modifier = Modifier.weight(1f))
                                    Text("Generowane: ${profile.generatedCount}", modifier = Modifier.weight(1f))
                                }
                                Row {
                                    Text("Wszystkie: ${profile.totalCount}", modifier = Modifier.weight(1f))
                                    Text("Dni w app: ${profile.activeDays}", modifier = Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                BadgeCategoryScrollable("Customowe", profile.badges.filter { it.name.startsWith("Custom") })
                                BadgeCategoryScrollable("Generowane", profile.badges.filter { it.name.startsWith("Generated") })
                                BadgeCategoryScrollable("Wszystkie", profile.badges.filter { it.name.startsWith("Total") })
                                BadgeCategoryScrollable("Dni aktywności", profile.badges.filter { it.name.startsWith("Days") })
                            }
                            else -> Text("Brak danych odznak.")
                        }
                    }
                }

                item {
                    ExpandableCard(
                        title = "Wideo",
                        expanded = expandedVideo,
                        onToggle = { expandedVideo = !expandedVideo }
                    ) {
                        LocalVideoPlayer(
                            resId = com.example.swimpal.R.raw.instruktaz,
                            title = "Instruktaż pływania – 18 minut",
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                item {
                    ExpandableCard(
                        title = "Historia treningów",
                        expanded = expandedHistory,
                        onToggle = { expandedHistory = !expandedHistory }
                    ) {
                        TrainingHistorySection(
                            historyTrainings = historyTrainings,
                            expandedHistoryTrainings = expandedHistoryTrainings
                        )
                    }
                }
            }

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Wyloguj się")
            }
        }
    }
}

@Composable
private fun TrainingHistorySection(
    historyTrainings: List<Training>,
    expandedHistoryTrainings: MutableMap<String, Boolean>
) {
    if (historyTrainings.isEmpty()) {
        Text("Brak historii treningów")
    } else {
        historyTrainings.forEach { training ->
            val expanded = expandedHistoryTrainings[training.id] ?: false
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedHistoryTrainings[training.id] = !expanded }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = training.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(if (expanded) "▲" else "▼")
                }

                if (training.creationDate.isNotBlank()) {
                    Text(
                        text = "Utworzono: ${formatDate(training.creationDate)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (!training.completedDate.isNullOrBlank()) {
                    Text(
                        text = "Ukończono: ${formatDate(training.completedDate)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "Ocena: ${training.rating ?: 0}/5",
                    style = MaterialTheme.typography.bodySmall
                )

                if (!training.note.isNullOrBlank()) {
                    Text(
                        text = "Notatka: ${training.note}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column {
                        training.days.forEachIndexed { dayIdx, day ->
                            Text(
                                text = "Dzień ${dayIdx + 1}",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                            )
                            day.tasks.sortedBy { it.order }.forEach { task ->
                                Text(
                                    text = "${task.order}. ${task.name}",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                                Text(
                                    text = "Opis: ${task.description}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        if (dateString.isBlank()) return ""
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = input.parse(dateString)
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            .format(date ?: return dateString)
    } catch (e: Exception) {
        dateString
    }
}
