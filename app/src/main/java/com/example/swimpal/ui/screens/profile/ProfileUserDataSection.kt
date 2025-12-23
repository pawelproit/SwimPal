package com.example.swimpal.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import com.example.swimpal.model.UserProfile
import java.text.SimpleDateFormat
import java.util.*

/**
 * Editable user profile data section with form fields and validation.
 *
 * Provides form inputs for first name, last name, birth date (date picker), and gender dropdown.
 * Supports real-time editing with save callback. Uses Material3 date picker dialog and exposed dropdown.
 *
 * @param profile Current [UserProfile] data to populate form fields.
 * @param onProfileChanged Callback invoked when user saves profile changes with updated [UserProfile].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileUserDataSection(
    profile: UserProfile,
    onProfileChanged: (UserProfile) -> Unit
) {
    var firstName by remember { mutableStateOf(profile.firstName) }
    var lastName by remember { mutableStateOf(profile.lastName) }
    var birthDate by remember { mutableStateOf(profile.birthDate) }
    var gender by remember { mutableStateOf(profile.gender) }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var expandedGender by remember { mutableStateOf(false) }

    val displayDateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val storageDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun parseBirthDate(dateStr: String): Long? = try {
        storageDateFormat.parse(dateStr)?.time
    } catch (e: Exception) { null }

    LaunchedEffect(birthDate) { selectedDateMillis = parseBirthDate(birthDate) }

    val displayBirthDate = if (birthDate.isNotBlank()) try {
        val date = storageDateFormat.parse(birthDate)
        displayDateFormat.format(date ?: Date())
    } catch (e: Exception) { birthDate } else ""

    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Imię") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Nazwisko") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(
            value = displayBirthDate,
            onValueChange = {},
            label = { Text("Data urodzenia") },
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            readOnly = true,
            singleLine = true
        )

        ExposedDropdownMenuBox(
            expanded = expandedGender,
            onExpandedChange = { expandedGender = !expandedGender }
        ) {
            OutlinedTextField(
                value = gender.ifEmpty { "Wybierz płeć" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Płeć") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = if (expandedGender) Color.Blue else Color.Gray
                    )
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedGender,
                onDismissRequest = { expandedGender = false }
            ) {
                listOf("Mężczyzna", "Kobieta", "Inne").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender = option
                            expandedGender = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }


        Button(onClick = { onProfileChanged(profile.copy(firstName = firstName.trim(), lastName = lastName.trim(), birthDate = birthDate, gender = gender)) }, modifier = Modifier.fillMaxWidth()) {
            Text("💾 Zapisz zmiany")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Wybierz datę urodzenia") },
            text = { DatePicker(state = datePickerState, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        birthDate = storageDateFormat.format(Date(millis))
                        selectedDateMillis = millis
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Anuluj") } }
        )
    }
}
