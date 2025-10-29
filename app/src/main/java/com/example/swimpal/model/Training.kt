package com.example.swimpal.model

data class Training(
    val id: String = "",
    val name: String = "",
    val days: List<TrainingDay> = emptyList(),
    val creationDate: String = "",
    val completedDate: String? = null,
    val rating: Int? = null,
    val note: String? = null
)

data class TrainingDay(
    val dayName: String = "",
    val tasks: List<TrainingTask> = emptyList()
)

data class TrainingTask(
    val name: String = "",
    val description: String = "",
    val order: Int = 0
)
