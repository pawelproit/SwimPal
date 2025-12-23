package com.example.swimpal.model

data class TrainingDay(
    val dayName: String = "",
    val tasks: List<TrainingTask> = emptyList()
)
