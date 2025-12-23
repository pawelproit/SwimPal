package com.example.swimpal.model

/**
 * Represents a user profile stored in Firestore and used across the application.
 *
 * @property uid Unique identifier of the user in Firebase Authentication.
 * @property email Email address associated with the user account.
 * @property firstName User's first name.
 * @property lastName User's last name.
 * @property birthDate User's date of birth stored as a string (for example "yyyy-MM-dd").
 * @property gender User's gender as selected in the profile.
 * @property customCount Number of custom trainings created by the user.
 * @property generatedCount Number of generated trainings created for the user.
 * @property totalCount Total number of trainings (custom + generated).
 * @property activeDays Number of unique days on which the user was active (had trainings).
 * @property trainingDates List of dates (as strings) when the user trained.
 * @property badges List of badges associated with the user and their achievement state.
 */
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
    val trainingDates: List<String> = emptyList(),
    val badges: List<Badge> = emptyList()
)
