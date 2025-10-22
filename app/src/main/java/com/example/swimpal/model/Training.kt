package com.example.swimpal.model

data class Training(
    val id: String = "",
    val name: String = "",
    val tasks: List<TrainingTask> = emptyList()
)

data class TrainingTask(
    val name: String = "",
    val description: String = ""
)
