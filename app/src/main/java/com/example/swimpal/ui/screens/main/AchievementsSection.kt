package com.example.swimpal.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.swimpal.model.Badge
import androidx.compose.ui.graphics.Color

/**
 * Displays up to three most recently achieved badges.
 *
 * Badges are filtered to include only achieved badges with non-null achieved date
 * and sorted by most recent first.
 *
 * @param badges List of user badges.
 */

@Composable
fun AchievementsSection(badges: List<Badge>) {
    val lastThree = badges
        .filter { it.achieved && it.achievedDate != null }
        .sortedByDescending { it.achievedDate }
        .take(3)

    if (lastThree.isEmpty()) return

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("🏆 Ostatnie osiągnięcia", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            lastThree.forEach { badge ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("✅", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(badge.name, style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                        Text(badge.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
