package com.example.swimpal.model

data class TrainingDayInput(
    val dayName: String,
    val tasks: List<TrainingTaskInput>
)

data class TrainingTaskInput(
    val name: String,
    val description: String
)
