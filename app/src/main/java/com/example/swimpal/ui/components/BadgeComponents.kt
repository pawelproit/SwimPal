package com.example.swimpal.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.swimpal.model.Badge

@Composable
fun BadgeCategoryScrollable(
    kategoria: String,
    badges: List<Badge>
) {
    if (badges.isEmpty()) return

    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(kategoria, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(6.dp))
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.Start
        ) {
            badges.forEach { BadgeItem(it) }
        }
    }
}

@Composable
fun BadgeItem(badge: Badge) {
    Card(
        modifier = Modifier
            .padding(end = 12.dp, bottom = 8.dp)
            .width(140.dp),
        colors = if (badge.achieved) {
            CardDefaults.cardColors()
        } else {
            CardDefaults.outlinedCardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (badge.achieved) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "Badge Icon",
                tint = if (badge.achieved) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(badge.name, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(badge.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
