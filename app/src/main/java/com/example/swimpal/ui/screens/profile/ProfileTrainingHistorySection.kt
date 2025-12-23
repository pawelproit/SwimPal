package com.example.swimpal.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swimpal.model.Training
import androidx.compose.material3.Divider

/**
 * Training history section displaying user's past training sessions.
 *
 * Renders expandable training cards showing training name, creation/completion dates, rating, notes,
 * and detailed day-by-day task breakdown. Supports empty state with placeholder illustration.
 *
 * @param historyTrainings List of completed training sessions to display.
 * @param expandedHistoryTrainings Mutable map tracking expanded/collapsed state for each training by ID.
 */
@Composable
fun TrainingHistorySection(
    historyTrainings: List<Training>,
    expandedHistoryTrainings: MutableMap<String, Boolean>
) {
    if (historyTrainings.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text("📭", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Brak historii treningów", color = Color(0xFF999999), fontSize = 16.sp)
            }
        }
    } else {
        historyTrainings.forEach { training ->
            val expanded = expandedHistoryTrainings[training.id] ?: false
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth().clickable { expandedHistoryTrainings[training.id] = !expanded }, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(training.name, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, modifier = Modifier.weight(1f))
                    Text(if (expanded) "▲" else "▼", color = Color(0xFF2196F3), fontSize = 18.sp)
                }

                if (training.creationDate.isNotBlank()) Text("🗓️ Utworzono: ${formatDate(training.creationDate)}", fontSize = 12.sp, color = Color(0xFF999999))
                if (!training.completedDate.isNullOrBlank()) Text("✅ Ukończono: ${formatDate(training.completedDate)}", fontSize = 12.sp, color = Color(0xFF999999))
                Text("⭐ Ocena: ${training.rating ?: 0}/5", fontSize = 12.sp, color = Color(0xFF999999))
                if (!training.note.isNullOrBlank()) Text("📝 Notatka: ${training.note}", fontSize = 12.sp, color = Color(0xFF999999))

                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        training.days.forEachIndexed { dayIdx, day ->
                            Text("Dzień ${dayIdx + 1}", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF2196F3), modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                            day.tasks.sortedBy { it.order }.forEach { task ->
                                Text("${task.order}. ${task.name}", fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(start = 16.dp))
                                Text("Opis: ${task.description}", fontSize = 12.sp, color = Color(0xFF999999), modifier = Modifier.padding(start = 16.dp, bottom = 4.dp))
                            }
                        }
                    }
                }
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
            }
        }
    }
}

/**
 * Formats date string from "yyyy-MM-dd" storage format to "dd.MM.yyyy" display format.
 *
 * Handles parsing errors gracefully by returning original string if format conversion fails.
 *
 * @param dateString Date string in "yyyy-MM-dd" format.
 * @return Formatted date string in "dd.MM.yyyy" format or original string if parsing fails.
 */
private fun formatDate(dateString: String): String {
    return try {
        if (dateString.isBlank()) return ""
        val input = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val date = input.parse(dateString)
        java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(date ?: return dateString)
    } catch (e: Exception) { dateString }
}
