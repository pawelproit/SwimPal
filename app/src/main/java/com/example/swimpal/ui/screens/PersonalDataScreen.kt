package com.example.swimpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.swimpal.model.UserProfile
import com.example.swimpal.viewmodel.ProfileState
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(
    profileState: ProfileState,
    onSave: (UserProfile) -> Unit,
    onSuccess: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Imię") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Nazwisko") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Data urodzenia (YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(
            expanded = genderExpanded,
            onExpandedChange = { genderExpanded = !genderExpanded }
        ) {
            TextField(
                value = gender,
                onValueChange = {},
                readOnly = true,
                label = { Text("Płeć") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = genderExpanded,
                onDismissRequest = { genderExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Mężczyzna") },
                    onClick = {
                        gender = "Mężczyzna"
                        genderExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Kobieta") },
                    onClick = {
                        gender = "Kobieta"
                        genderExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Inna") },
                    onClick = {
                        gender = "Inna"
                        genderExpanded = false
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        when (profileState) {
            is ProfileState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is ProfileState.Success -> onSuccess()
            else -> Button(
                onClick = {
                    if (firstName.isNotBlank() && lastName.isNotBlank() && birthDate.isNotBlank() && gender.isNotBlank()) {
                        onSave(
                            UserProfile(
                                firstName = firstName,
                                lastName = lastName,
                                birthDate = birthDate,
                                gender = gender
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zapisz dane")
            }
        }

        if (profileState is ProfileState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = profileState.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
