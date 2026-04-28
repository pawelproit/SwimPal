package com.example.swimpal.model

/**
 * Represents a training plan stored in Firestore.
 *
 * @property id Unique identifier of the training in the database.
 * @property name Human‑readable name of the training plan.
 * @property type Type of the training (for example "Sprint", "Open Water", "Custom").
 * @property days Ordered list of training days that belong to this plan.
 * @property creationDate Date when the training was created, stored as a string.
 * @property completedDate Date when the training was completed, or null if it has not been completed.
 * @property rating Optional user rating of the training on a numeric scale.
 * @property note Optional user note or comment about the training.
 */
data class Training(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val days: List<TrainingDay> = emptyList(),
    val creationDate: String = "",
    val completedDate: String? = null,
    val rating: Int? = null,
    val note: String? = null
)
