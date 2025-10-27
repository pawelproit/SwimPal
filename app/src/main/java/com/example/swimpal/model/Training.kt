package com.example.swimpal.model

data class Training(
    val id: String = "",
    val name: String = "",
    val days: List<TrainingDay> = emptyList()
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
