package com.example.swimpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.ProfileState
import com.example.swimpal.viewmodel.UserProfileViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.content.Context

@Composable
fun MainScreen(
    userProfileViewModel: UserProfileViewModel = viewModel(),
    onNavigateToGenerate: () -> Unit = {},
    onNavigateToTraining: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val profileState by userProfileViewModel.profileState.collectAsState()
    val context = LocalContext.current
    var showBirthdayDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userProfileViewModel.loadUserProfile()
    }

    LaunchedEffect(profileState) {
        val currentDate = LocalDate.now()
        if (profileState is ProfileState.Success) {
            val profile = (profileState as ProfileState.Success).userProfile
            val birthDate = profile.birthDate
            val formats = listOf(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
            )
            val isBirthday = formats.any { format ->
                try {
                    birthDate?.let {
                        val parsed = LocalDate.parse(it, format)
                        parsed.monthValue == currentDate.monthValue && parsed.dayOfMonth == currentDate.dayOfMonth
                    } ?: false
                } catch (_: Exception) {
                    false
                }
            }
            val prefs = context.getSharedPreferences("birth_prefs", Context.MODE_PRIVATE)
            val lastShown = prefs.getString("last_birthday_greeting", null)
            if (isBirthday && lastShown != currentDate.toString()) {
                showBirthdayDialog = true
                prefs.edit().putString("last_birthday_greeting", currentDate.toString()).apply()
            }
        }
    }

    when (profileState) {
        is ProfileState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ładowanie profilu...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        is ProfileState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("❌", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Błąd ładowania danych",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Spróbuj ponownie później",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )

                }
            }
        }
        is ProfileState.Success -> {
            val profile = (profileState as ProfileState.Success).userProfile


            MainScreenContent(
                profile = profile,
                onNavigateToGenerate = onNavigateToGenerate,
                onNavigateToTraining = onNavigateToTraining,
                onNavigateToHistory = onNavigateToHistory
            )
        }
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    if (showBirthdayDialog) {
        AlertDialog(
            onDismissRequest = { showBirthdayDialog = false },
            confirmButton = {
                Button(onClick = { showBirthdayDialog = false }) {
                    Text("Miłego dnia!")
                }
            },
            title = { Text("Wszystkiego najlepszego! 🎉") },
            text = { Text("Z okazji Twoich urodzin życzymy Ci samych sukcesów i świetnych treningów!") }
        )
    }
}

@Composable
fun MainScreenContent(
    profile: com.example.swimpal.model.UserProfile,
    onNavigateToGenerate: () -> Unit,
    onNavigateToTraining: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        GreetingSection(userName = profile.firstName)

        Spacer(modifier = Modifier.height(24.dp))

        StatsSection(
            totalCount = profile.totalCount,
            activeDays = profile.activeDays,
            currentStreak = profile.currentStreak
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuickActionsSection(
            onGenerateClick = onNavigateToGenerate,
            onTrainingsClick = onNavigateToTraining,
            onHistoryClick = onNavigateToHistory
        )

        Spacer(modifier = Modifier.height(24.dp))

        WeeklyGoalCard(
            completedTrainings = profile.totalCount,
            goalTrainings = 4
        )

        Spacer(modifier = Modifier.height(24.dp))

        AchievementsSection(badges = profile.badges.filter { it.achieved })
    }
}

@Composable
fun GreetingSection(userName: String) {
    Column {
        Text(
            text = "👋 Cześć, $userName!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "\"Każdy trening zbliża Cię do celu\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatsSection(totalCount: Int, activeDays: Int, currentStreak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 Twoje statystyki",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(icon = "🏅", value = totalCount.toString(), label = "Treningów")
                StatItem(icon = "🔥", value = currentStreak.toString(), label = "Dni z rzędu")
                StatItem(icon = "📅", value = activeDays.toString(), label = "Aktywnych dni")
            }
        }
    }
}

@Composable
fun StatItem(icon: String, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun QuickActionsSection(
    onGenerateClick: () -> Unit,
    onTrainingsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Column {
        Text(
            text = "🚀 Szybkie akcje",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Add,
                label = "Generuj\nTrening",
                onClick = onGenerateClick
            )
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.List,
                label = "Moje\nTreningi",
                onClick = onTrainingsClick
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Create,
                label = "Napisz\nTrening",
                onClick = onGenerateClick
            )
            QuickActionButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DateRange,
                label = "Historia",
                onClick = onHistoryClick
            )
        }
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(100.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun WeeklyGoalCard(completedTrainings: Int, goalTrainings: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🎯 Cel tygodnia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "$completedTrainings / $goalTrainings treningów",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (completedTrainings.toFloat() / goalTrainings).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun AchievementsSection(badges: List<com.example.swimpal.model.Badge>) {
    if (badges.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🏆 Ostatnie osiągnięcia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            badges.take(3).forEach { badge ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✅", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = badge.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = badge.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
