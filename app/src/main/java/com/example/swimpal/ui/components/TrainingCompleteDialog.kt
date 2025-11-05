package com.example.swimpal.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TrainingCompleteDialog(
    show: Boolean,
    rating: Int,
    note: String,
    isCompleting: Boolean,
    onRatingChange: (Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = { if (!isCompleting) onDismiss() },
            title = { Text("Ocena treningu") },
            text = {
                Column {
                    Text("Jak oceniasz ten trening?")
                    Row(
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    ) {
                        (1..5).forEach { value ->
                            Button(
                                onClick = { onRatingChange(value) },
                                colors = if (rating == value) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                                modifier = Modifier.padding(end = 4.dp)
                            ) { Text("$value") }
                        }
                    }
                    OutlinedTextField(
                        value = note,
                        onValueChange = onNoteChange,
                        label = { Text("Notatka (opcjonalnie)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isCompleting
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm, enabled = !isCompleting) {
                    if (isCompleting) {
                        Text("Zapisywanie...")
                    } else {
                        Text("Zapisz")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isCompleting
                ) { Text("Anuluj") }
            }
        )
    }
}
