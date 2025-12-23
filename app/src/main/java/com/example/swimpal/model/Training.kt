package com.example.swimpal.model

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
