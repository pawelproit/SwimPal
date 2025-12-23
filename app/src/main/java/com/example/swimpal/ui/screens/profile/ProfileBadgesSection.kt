package com.example.swimpal.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.swimpal.ui.components.BadgeCategoryScrollable
import com.example.swimpal.viewmodel.ProfileState

/**
 * Profile badges statistics section displaying user achievements and activity metrics.
 *
 * Shows badge counts (custom, generated, total), active days in app, and categorized badge collections.
 * Handles different [ProfileState] variants, displaying "No badge data" for non-success states.
 *
 * @param profileState Current profile state containing user profile data with badges and statistics.
 */
@Composable
fun ProfileBadgesSection(profileState: ProfileState) {
    when (profileState) {
        is ProfileState.Success -> {
            val profile = profileState.userProfile
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "📊 Statystyki", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text("Custom: ${profile.customCount}", modifier = Modifier.weight(1f), fontSize = 14.sp)
                Text("Generowane: ${profile.generatedCount}", modifier = Modifier.weight(1f), fontSize = 14.sp)
            }
            Row {
                Text("Wszystkie: ${profile.totalCount}", modifier = Modifier.weight(1f), fontSize = 14.sp)
                Text("Dni w app: ${profile.activeDays}", modifier = Modifier.weight(1f), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            BadgeCategoryScrollable("💪 Customowe", profile.badges.filter { it.name.startsWith("Custom") })
            BadgeCategoryScrollable("🤖 Generowane", profile.badges.filter { it.name.startsWith("Generated") })
            BadgeCategoryScrollable("🎯 Wszystkie", profile.badges.filter { it.name.startsWith("Total") })
            BadgeCategoryScrollable("📅 Dni aktywności", profile.badges.filter { it.name.startsWith("Days") })
        }
        else -> Text("Brak danych odznak.", fontSize = 14.sp)
    }
}
