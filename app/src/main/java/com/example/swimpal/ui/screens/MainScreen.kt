package com.example.swimpal.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    onNavigateToGenerate: (String?) -> Unit = { _ -> },
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

            val isBirthday = birthDate.isNotBlank() && run {
                try {
                    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val parsed = LocalDate.parse(birthDate, format)
                    parsed.monthValue == currentDate.monthValue &&
                            parsed.dayOfMonth == currentDate.dayOfMonth
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
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ładowanie profilu...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        is ProfileState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("❌", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Błąd ładowania danych",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
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
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    if (showBirthdayDialog) {
        AlertDialog(
            onDismissRequest = { showBirthdayDialog = false },
            confirmButton = {
                Button(
                    onClick = { showBirthdayDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Miłego dnia!")
                }
            },
            title = { Text("Wszystkiego najlepszego! 🎉") },
            text = { Text("Z okazji Twoich urodzin życzymy Ci samych sukcesów i świetnych treningów!") },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun MainScreenContent(
    profile: com.example.swimpal.model.UserProfile,
    onNavigateToGenerate: (String?) -> Unit,
    onNavigateToTraining: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            GreetingSection(userName = profile.firstName)

            Spacer(modifier = Modifier.height(24.dp))

            StatsSection(
                totalCount = profile.totalCount,
                activeDays = profile.activeDays
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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GreetingSection(userName: String?) {
    Column {
        Text(
            text = "👋 Cześć${if (!userName.isNullOrBlank()) ", $userName" else ""}!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "\"Każdy trening zbliża Cię do celu\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun StatsSection(totalCount: Int, activeDays: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📊 Twoje statystyki",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(icon = "🏅", value = totalCount.toString(), label = "Treningów")
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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
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
    onGenerateClick: (String?) -> Unit,
    onTrainingsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Column {
        Text(
            text = "🚀 Szybkie akcje",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
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
                onClick = { onGenerateClick(null) }
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
                onClick = {
                    onGenerateClick("custom")
                }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
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
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun WeeklyGoalCard(completedTrainings: Int, goalTrainings: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🎯 Cel tygodnia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "$completedTrainings / $goalTrainings treningów",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (completedTrainings.toFloat() / goalTrainings).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
fun AchievementsSection(badges: List<com.example.swimpal.model.Badge>) {
    val lastThree = badges
        .filter { it.achieved && it.achievedDate != null }
        .sortedByDescending { it.achievedDate }
        .take(3)

    if (lastThree.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🏆 Ostatnie osiągnięcia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            lastThree.forEach { badge ->
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
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
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
