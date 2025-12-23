package com.example.swimpal.model

/**
 * Represents a single day within a training plan.
 *
 * @property dayName Name of the day (for example "Day 1", "Technique day").
 * @property tasks Ordered list of tasks that should be performed on this day.
 */
data class TrainingDay(
    val dayName: String = "",
    val tasks: List<TrainingTask> = emptyList()
)
