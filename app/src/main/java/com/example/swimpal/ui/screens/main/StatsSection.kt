package com.example.swimpal.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

/**
 * Section displaying basic user statistics.
 *
 * Shows total trainings completed and number of active days.
 *
 * @param totalCount Total number of completed trainings.
 * @param activeDays Number of unique days with activity.
 */

@Composable
fun StatsSection(totalCount: Int, activeDays: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("📊 Twoje statystyki", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("🏅", totalCount.toString(), "Treningów")
                StatItem("📅", activeDays.toString(), "Aktywnych dni")
            }
        }
    }
}

/**
 * Single statistic item used inside [StatsSection].
 *
 * @param icon Icon or emoji representing the statistic.
 * @param value Value to display.
 * @param label Description of the statistic.
 */

@Composable
fun StatItem(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, style = MaterialTheme.typography.headlineMedium)
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
