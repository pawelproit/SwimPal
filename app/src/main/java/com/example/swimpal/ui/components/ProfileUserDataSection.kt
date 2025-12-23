package com.example.swimpal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.swimpal.model.UserProfile

/**
 * Section that allows the user to view and edit basic profile information.
 *
 * Email is displayed as read‑only, while first name, last name, birth date
 * and gender can be edited and saved back to the parent via [onProfileChanged].
 *
 * @param profile Current user profile values.
 * @param onProfileChanged Callback invoked with an updated [UserProfile] when user saves changes.
 */
@Composable
fun ProfileUserDataSection(
    profile: UserProfile,
    onProfileChanged: (UserProfile) -> Unit
) {
    var firstName by remember(profile) { mutableStateOf(profile.firstName ?: "") }
    var lastName by remember(profile) { mutableStateOf(profile.lastName ?: "") }
    var birthDate by remember(profile) { mutableStateOf(profile.birthDate ?: "") }
    var gender by remember(profile) { mutableStateOf(profile.gender ?: "") }
    val email = profile.email ?: ""

    Column {
        OutlinedTextField(
            value = email,
            onValueChange = {},
            readOnly = true,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Imię") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Nazwisko") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Data urodzenia") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = gender,
            onValueChange = { gender = it },
            label = { Text("Płeć") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                onProfileChanged(
                    profile.copy(
                        firstName = firstName,
                        lastName = lastName,
                        birthDate = birthDate,
                        gender = gender
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zapisz zmiany")
        }
    }
}
