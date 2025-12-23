package com.example.swimpal.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

/**
 * Section providing quick-access buttons for key actions.
 *
 * Includes buttons to generate a training, view user's trainings,
 * write a custom training, and open the training history.
 *
 * @param onGenerateClick Callback invoked when generating or writing a training.
 * @param onTrainingsClick Callback invoked when opening the user's trainings list.
 * @param onHistoryClick Callback invoked when opening the training history/profile.
 */

@Composable
fun QuickActionsSection(
    onGenerateClick: (String?) -> Unit,
    onTrainingsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Column {
        Text("🚀 Szybkie akcje", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionButton(Modifier.weight(1f), Icons.Default.Add, "Generuj\nTrening") { onGenerateClick(null) }
            QuickActionButton(Modifier.weight(1f), Icons.Default.List, "Moje\nTreningi", onTrainingsClick)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionButton(Modifier.weight(1f), Icons.Default.Create, "Napisz\nTrening") { onGenerateClick("custom") }
            QuickActionButton(Modifier.weight(1f), Icons.Default.DateRange, "Historia", onHistoryClick)
        }
    }
}

/**
 * Reusable card-style button for the quick actions section.
 *
 * @param modifier Modifier applied to the card.
 * @param icon Icon representing the action.
 * @param label Text label shown under the icon (supports line breaks).
 * @param onClick Callback invoked when the card is tapped.
 */

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(100.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
