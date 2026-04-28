package com.example.swimpal.model

/**
 * Represents an achievement that a user can earn in the application.
 *
 * @property name Name of the badge.
 * @property description Short description of the badge condition.
 * @property achieved Indicates whether the user has already obtained this badge.
 * @property isNew Indicates whether the badge has been obtained recently and should be highlighted.
 * @property achievedDate Date and time when the badge was achieved, in string format, or null if not achieved yet.
 */
data class Badge(
    val name: String = "",
    val description: String = "",
    val achieved: Boolean = false,
    val isNew: Boolean = false,
    val achievedDate: String? = null
)
