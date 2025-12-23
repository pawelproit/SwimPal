package com.example.swimpal.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*

/**
 * Confirmation dialog for deleting a training.
 *
 * Asks the user whether the selected training should be removed and exposes
 * callbacks for both confirmation and dismissal.
 *
 * @param show Whether the dialog should currently be visible.
 * @param onConfirm Called when the user confirms deletion.
 * @param onDismiss Called when the user cancels or closes the dialog.
 */
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
