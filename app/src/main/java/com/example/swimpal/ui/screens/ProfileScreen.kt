package com.example.swimpal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
                        Text(
                            text = "👤 Twój profil",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Zarządzaj swoim kontem",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        ExpandableCard(
                            title = "📝 Dane osobowe",
                            expanded = expandedData,
                            onToggle = { expandedData = !expandedData }
                        ) {
                            when (profileState) {
                                is ProfileState.Loading -> Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary
                                    )
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
                            title = "🏆 Odznaki i osiągnięcia",
                            expanded = expandedBadges,
                            onToggle = { expandedBadges = !expandedBadges }
                        ) {
                            when (profileState) {
                                is ProfileState.Success -> {
                                    val profile = (profileState as ProfileState.Success).userProfile
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "📊 Statystyki",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        Text(
                                            "Custom: ${profile.customCount}",
                                            modifier = Modifier.weight(1f),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            "Generowane: ${profile.generatedCount}",
                                            modifier = Modifier.weight(1f),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row {
                                        Text(
                                            "Wszystkie: ${profile.totalCount}",
                                            modifier = Modifier.weight(1f),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            "Dni w app: ${profile.activeDays}",
                                            modifier = Modifier.weight(1f),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    BadgeCategoryScrollable(
                                        "💪 Customowe",
                                        profile.badges.filter { it.name.startsWith("Custom") }
                                    )
                                    BadgeCategoryScrollable(
                                        "🤖 Generowane",
                                        profile.badges.filter { it.name.startsWith("Generated") }
                                    )
                                    BadgeCategoryScrollable(
                                        "🎯 Wszystkie",
                                        profile.badges.filter { it.name.startsWith("Total") }
                                    )
                                    BadgeCategoryScrollable(
                                        "📅 Dni aktywności",
                                        profile.badges.filter { it.name.startsWith("Days") }
                                    )
                                }
                                else -> Text("Brak danych odznak.")
                            }
                        }
                    }

                    item {
                        ExpandableCard(
                            title = "🎥 Instruktaż wideo",
                            expanded = expandedVideo,
                            onToggle = { expandedVideo = !expandedVideo }
                        ) {
                            val baseUrl = "https://pawelproit.github.io/swimpal-videos/videos"

                            val videoCategories = mapOf(
                                "Kraul" to listOf(
                                    "$baseUrl/kraul/kraul.mp4" to "Nogi w kraulu"
                                ),
                                "Żaba" to listOf(
                                    "$baseUrl/kraul/kraulW.mp4" to "Pełna technika żabki"
                                ),
                                "Grzbiet" to listOf(
                                    "$baseUrl/grzbiet/grzbiet.mp4" to "Styl grzbietowy"
                                ),
                                "Ćwiczenia" to listOf(
                                    "$baseUrl/cwiczenia/pozcyja_torpedowa.mp4" to "Pozycja torpedowa"
                                )
                            )

                            var expandedCategories by remember { mutableStateOf(setOf<String>()) }

                            Column(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                videoCategories.forEach { (categoryName, videos) ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedCategories =
                                                    if (categoryName in expandedCategories) {
                                                        expandedCategories - categoryName
                                                    } else {
                                                        expandedCategories + categoryName
                                                    }
                                            },
                                        shape = MaterialTheme.shapes.medium,
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = categoryName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.weight(1f),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (categoryName in expandedCategories) "▲" else "▼",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    if (categoryName in expandedCategories) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        videos.forEach { (url, title) ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surface
                                                )
                                            ) {
                                                NetworkVideoPlayer(
                                                    videoUrl = url,
                                                    title = title,
                                                    modifier = Modifier.padding(12.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }


                    item {
                        ExpandableCard(
                            title = "📜 Historia treningów",
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
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "🚪 Wyloguj się",
                        fontWeight = FontWeight.Bold
                    )
                }
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "📭", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Brak historii treningów",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (expanded) "▲" else "▼",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (training.creationDate.isNotBlank()) {
                    Text(
                        text = "🗓️ Utworzono: ${formatDate(training.creationDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!training.completedDate.isNullOrBlank()) {
                    Text(
                        text = "✅ Ukończono: ${formatDate(training.completedDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "⭐ Ocena: ${training.rating ?: 0}/5",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!training.note.isNullOrBlank()) {
                    Text(
                        text = "📝 Notatka: ${training.note}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        training.days.forEachIndexed { dayIdx, day ->
                            Text(
                                text = "Dzień ${dayIdx + 1}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                            day.tasks.sortedBy { it.order }.forEach { task ->
                                Text(
                                    text = "${task.order}. ${task.name}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                                Text(
                                    text = "Opis: ${task.description}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
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
