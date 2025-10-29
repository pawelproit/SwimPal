package com.example.swimpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.ProfileState
import com.example.swimpal.viewmodel.UserProfileViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@Composable
fun MainScreen(
    userProfileViewModel: UserProfileViewModel = viewModel()
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

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Witaj w aplikacji!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showBirthdayDialog) {
        AlertDialog(
            onDismissRequest = { showBirthdayDialog = false },
            confirmButton = {
                Button(onClick = { showBirthdayDialog = false }) {
                    Text("Miłego dnia!")
                }
            },
            title = { Text("Wszystkiego najlepszego!") },
            text = { Text("Z okazji Twoich urodzin życzymy Ci samych sukcesów i świetnych treningów! 🎉") }
        )
    }
}

