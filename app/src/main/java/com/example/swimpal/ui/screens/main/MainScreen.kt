package com.example.swimpal.ui.screens.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.ProfileState
import com.example.swimpal.viewmodel.UserProfileViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Main home screen displaying user statistics, quick actions, and achievements.
 *
 * Loads the user profile on first composition, reacts to [ProfileState],
 * and shows a birthday greeting dialog at most once per day using SharedPreferences.
 *
 * @param userProfileViewModel ViewModel providing profile state.
 * @param onNavigateToGenerate Callback for training generation screen.
 * @param onNavigateToTraining Callback for training list screen.
 * @param onNavigateToHistory Callback for history/profile screen.
 */

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
        is ProfileState.Loading -> LoadingState()
        is ProfileState.Error -> ErrorState()
        is ProfileState.Success -> {
            val profile = (profileState as ProfileState.Success).userProfile
            MainScreenContent(
                profile = profile,
                onNavigateToGenerate = onNavigateToGenerate,
                onNavigateToTraining = onNavigateToTraining,
                onNavigateToHistory = onNavigateToHistory
            )
        }
        else -> LoadingState()
    }

    if (showBirthdayDialog) {
        AlertDialog(
            onDismissRequest = { showBirthdayDialog = false },
            confirmButton = {
                Button(
                    onClick = { showBirthdayDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Miłego dnia!") }
            },
            title = { Text("Wszystkiego najlepszego! 🎉") },
            text = { Text("Z okazji Twoich urodzin życzymy Ci samych sukcesów i świetnych treningów!") },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

/**
 * Composable displaying a full-screen loading state.
 *
 * Shows a circular progress indicator and a "Loading profile..." message
 * while the user profile data is being fetched.
 */

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Ładowanie profilu...", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

/**
 * Composable displaying a full-screen error state.
 *
 * Shown when there is an error fetching the user profile data.
 * Displays an error icon, error message, and suggestion to retry later.
 */

@Composable
private fun ErrorState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("❌", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Błąd ładowania danych", color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Spróbuj ponownie później", color = MaterialTheme.colorScheme.error)
        }
    }
}
