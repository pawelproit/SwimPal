package com.example.swimpal.model

/**
 * Represents a single training task performed during a specific training day.
 *
 * @property name Name of the exercise or task.
 * @property description Description of how to perform the task.
 * @property order Position of the task in the sequence within its training day.
 */
data class TrainingTask(
    val name: String = "",
    val description: String = "",
    val order: Int = 0
)
