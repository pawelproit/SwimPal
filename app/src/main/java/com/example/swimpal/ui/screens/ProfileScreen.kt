package com.example.swimpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.swimpal.viewmodel.UserProfileViewModel
import com.example.swimpal.viewmodel.ProfileState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(userProfileViewModel: UserProfileViewModel = viewModel()) {
    val profileState by userProfileViewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        userProfileViewModel.loadUserProfile()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        when(profileState) {
            is ProfileState.Success -> {
                val profile = (profileState as ProfileState.Success).userProfile
                Text("Imię: ${profile.firstName}")
                Text("Nazwisko: ${profile.lastName}")
                Text("Data urodzenia: ${profile.birthDate}")
                Text("Płeć: ${profile.gender}")
            }
            is ProfileState.Loading -> CircularProgressIndicator()
            is ProfileState.Error -> Text("Błąd: ${(profileState as ProfileState.Error).error}")
            else -> Text("Brak danych profilu.")
        }
    }
}
