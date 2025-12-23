package com.example.swimpal.model

/**
 * Represents user input for a single training day before it is converted to a persisted model.
 *
 * @property dayName Name of the training day (for example "Day 1").
 * @property tasks List of task inputs that belong to this training day.
 */
data class TrainingDayInput(
    val dayName: String,
    val tasks: List<TrainingTaskInput>
)

/**
 * Represents user input for a single training task before it is converted to a persisted model.
 *
 * @property name Name of the task (for example "Warm‑up").
 * @property description Detailed description of what should be done in this task.
 */
data class TrainingTaskInput(
    val name: String,
    val description: String
)
