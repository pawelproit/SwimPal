package com.example.swimpal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swimpal.viewmodel.ProfileState
import com.example.swimpal.viewmodel.TrainingViewModel
import com.example.swimpal.viewmodel.UserProfileViewModel
import com.example.swimpal.ui.components.ExpandableCard
import com.example.swimpal.ui.screens.profile.ProfileBadgesSection
import com.example.swimpal.ui.screens.profile.VideoSection
import com.example.swimpal.ui.screens.profile.ProfileUserDataSection
import com.example.swimpal.ui.screens.profile.TrainingHistorySection

/**
 * Main profile screen displaying comprehensive user account information and management options.
 *
 * Features expandable sections for personal data editing, badges/achievements, instructional videos,
 * and training history. Uses gradient background with LazyColumn for smooth scrolling. Handles profile
 * loading states and provides logout functionality.
 *
 * @param userProfileViewModel ViewModel managing user profile data and operations.
 * @param trainingViewModel ViewModel providing training history data.
 * @param onLogout Callback invoked when user requests logout.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfileViewModel: UserProfileViewModel = viewModel(),
    trainingViewModel: TrainingViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val profileState by userProfileViewModel.profileState.collectAsState()
    val historyTrainings by trainingViewModel.historyTrainings.collectAsState(initial = emptyList())
    val expandedHistoryTrainings = remember { mutableStateMapOf<String, Boolean>() }

    var expandedData by remember { mutableStateOf(false) }
    var expandedBadges by remember { mutableStateOf(false) }
    var expandedVideo by remember { mutableStateOf(false) }
    var expandedHistory by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userProfileViewModel.loadUserProfile()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                        ProfileHeader()
                    }

                    item {
                        ExpandableCard(title = "📝 Dane osobowe", expanded = expandedData, onToggle = { expandedData = !expandedData }) {
                            when (profileState) {
                                is ProfileState.Loading -> Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color(0xFF2196F3))
                                }
                                is ProfileState.Success -> {
                                    ProfileUserDataSection(
                                        profile = (profileState as ProfileState.Success).userProfile,
                                        onProfileChanged = { userProfileViewModel.saveUserProfile(it) }
                                    )
                                }
                                is ProfileState.Error -> Text(
                                    "Błąd: ${(profileState as ProfileState.Error).error}",
                                    color = Color.Red,
                                    fontSize = 14.sp
                                )
                                else -> Text("Brak danych profilu.", fontSize = 14.sp)
                            }
                        }
                    }

                    item {
                        ExpandableCard(title = "🏆 Odznaki i osiągnięcia", expanded = expandedBadges, onToggle = { expandedBadges = !expandedBadges }) {
                            ProfileBadgesSection(profileState)
                        }
                    }

                    item {
                        ExpandableCard(title = "🎥 Instruktaż wideo", expanded = expandedVideo, onToggle = { expandedVideo = !expandedVideo }) {
                            VideoSection()
                        }
                    }

                    item {
                        ExpandableCard(title = "📜 Historia treningów", expanded = expandedHistory, onToggle = { expandedHistory = !expandedHistory }) {
                            TrainingHistorySection(historyTrainings, expandedHistoryTrainings)
                        }
                    }
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(
                        text = "🚪 Wyloguj się",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 16.sp

                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader() {
    Column {
        androidx.compose.material3.Text(
            text = "👤 Twój profil",
            fontSize = 28.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = Color(0xFF1565C0)
        )
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Text(
            text = "Zarządzaj swoim kontem",
            fontSize = 16.sp,
            color = Color(0xFF666666)
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
