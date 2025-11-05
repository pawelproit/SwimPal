package com.example.swimpal.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun TrainingDeleteDialog(
    show: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) { Text("Tak") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Nie") }
            },
            title = { Text("Usuń trening") },
            text = { Text("Czy na pewno chcesz usunąć ten trening?") }
        )
    }
}
