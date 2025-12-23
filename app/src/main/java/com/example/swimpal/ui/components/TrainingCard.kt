package com.example.swimpal.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.swimpal.model.Training

/**
 * Card displaying a single training with expandable details and actions.
 *
 * The header shows the training name and creation date, and provides a delete button.
 * When [expanded] is true, all days and their tasks are shown, and the user can
 * mark the training as completed via [onCompleteClick].
 *
 * @param training Training data to display.
 * @param expanded Whether the card is currently expanded.
 * @param onExpandClick Callback invoked when the header is tapped to toggle expansion.
 * @param onDeleteClick Callback invoked when the delete icon is pressed.
 * @param onCompleteClick Callback invoked when the user marks the training as completed.
 */
@Composable
fun TrainingCard(
    training: Training,
    expanded: Boolean,
    onExpandClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    val expandedDays = remember { mutableStateMapOf<String, Boolean>() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandClick() }
                    .padding(end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val date = formatDate(training.creationDate)
                Text(
                    text = "${training.name}" + if (date.isNotBlank()) ", $date" else "",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Usuń trening")
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    training.days.forEachIndexed { idx, day ->
                        val dayKey = "${training.id}_$idx"
                        val expandedDay = expandedDays[dayKey] ?: false
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedDays[dayKey] = !expandedDay }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dzień ${idx + 1}",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (expandedDay) "▲" else "▼",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        AnimatedVisibility(
                            visible = expandedDay,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                day.tasks.sortedBy { it.order }.forEach { task ->
                                    Text(
                                        "${task.order}. ${task.name}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        "Opis: ${task.description}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onCompleteClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("Oznacz jako wykonane") }
                }
            }
        }
    }
}
