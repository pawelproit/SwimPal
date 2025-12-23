package com.example.swimpal.model

data class Badge(
    val name: String = "",
    val description: String = "",
    val achieved: Boolean = false,
    val isNew: Boolean = false,
    val achievedDate: String? = null
)
