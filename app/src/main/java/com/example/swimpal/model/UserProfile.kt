package com.example.swimpal.model

data class Badge(
    val name: String = "",
    val description: String = "",
    val achieved: Boolean = false
)

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val customCount: Int = 0,
    val generatedCount: Int = 0,
    val totalCount: Int = 0,
    val activeDays: Int = 0,
    val currentStreak: Int = 0,
    val trainingDates: List<String> = emptyList(),
    val badges: List<Badge> = emptyList()
)
