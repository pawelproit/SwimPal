package com.example.swimpal.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.swimpal.model.UserProfile
import com.example.swimpal.model.Badge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Represents the state of user profile loading and persistence.
 *
 * - [Idle]     No profile operation is currently in progress.
 * - [Loading] Profile data is being loaded or saved.
 * - [Success] Profile operation completed successfully and provides [UserProfile].
 * - [Error]   Profile operation failed with a user-readable error message.
 */
sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val userProfile: UserProfile) : ProfileState()
    data class Error(val error: String) : ProfileState()
}

/**
 * Represents UI state related to badge notifications.
 *
 * @property showDialog Indicates whether the badge dialog should be displayed.
 * @property badgeName Name of the newly achieved badge.
 * @property badgeDescription Description of the newly achieved badge.
 */
data class BadgeState(
    val showDialog: Boolean = false,
    val badgeName: String = "",
    val badgeDescription: String = ""
)

/**
 * ViewModel responsible for managing user profile data and achievements.
 *
 * Handles:
 * - Loading and saving user profile data in Firestore.
 * - Calculating and updating badges based on user statistics.
 * - Detecting newly achieved badges and triggering UI notifications.
 *
 * Exposes profile and badge states in a reactive form for the UI layer.
 */
class UserProfileViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUid get() = auth.currentUser?.uid
    private val currentEmail get() = auth.currentUser?.email.orEmpty()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)

    /**
     * Public profile state observed by the UI.
     */
    val profileState: StateFlow<ProfileState> = _profileState

    private val _badgeState = mutableStateOf(BadgeState())

    /**
     * Public badge UI state observed by Compose.
     */
    val badgeState: State<BadgeState> = _badgeState

    // Badge identifiers
    private val BADGE_CUSTOM_5 = "Custom 5"
    private val BADGE_CUSTOM_10 = "Custom 10"
    private val BADGE_CUSTOM_20 = "Custom 20"
    private val BADGE_CUSTOM_50 = "Custom 50"

    private val BADGE_GENERATED_5 = "Generated 5"
    private val BADGE_GENERATED_10 = "Generated 10"
    private val BADGE_GENERATED_20 = "Generated 20"
    private val BADGE_GENERATED_50 = "Generated 50"

    private val BADGE_TOTAL_20 = "Total 20"
    private val BADGE_TOTAL_50 = "Total 50"
    private val BADGE_TOTAL_100 = "Total 100"

    private val BADGE_DAYS_5 = "Days 5"
    private val BADGE_DAYS_20 = "Days 20"
    private val BADGE_DAYS_50 = "Days 50"

    /**
     * Builds a list of default badges based on user statistics.
     *
     * @param custom Number of custom trainings created.
     * @param generated Number of generated trainings created.
     * @param total Total number of trainings.
     * @param days Number of active training days.
     * @return List of calculated [Badge] objects.
     */
    private fun defaultBadges(
        custom: Int,
        generated: Int,
        total: Int,
        days: Int
    ): List<Badge> = listOf(
        Badge(BADGE_CUSTOM_5, "5 treningów własnych", custom >= 5),
        Badge(BADGE_CUSTOM_10, "10 treningów własnych", custom >= 10),
        Badge(BADGE_CUSTOM_20, "20 treningów własnych", custom >= 20),
        Badge(BADGE_CUSTOM_50, "50 treningów własnych", custom >= 50),
        Badge(BADGE_GENERATED_5, "5 treningów generowanych", generated >= 5),
        Badge(BADGE_GENERATED_10, "10 treningów generowanych", generated >= 10),
        Badge(BADGE_GENERATED_20, "20 treningów generowanych", generated >= 20),
        Badge(BADGE_GENERATED_50, "50 treningów generowanych", generated >= 50),
        Badge(BADGE_TOTAL_20, "20 treningów łącznie", total >= 20),
        Badge(BADGE_TOTAL_50, "50 treningów łącznie", total >= 50),
        Badge(BADGE_TOTAL_100, "100 treningów łącznie", total >= 100),
        Badge(BADGE_DAYS_5, "5 aktywnych dni", days >= 5),
        Badge(BADGE_DAYS_20, "20 aktywnych dni", days >= 20),
        Badge(BADGE_DAYS_50, "50 aktywnych dni", days >= 50)
    )

    /**
     * Checks for newly achieved badges and updates Firestore if needed.
     *
     * If a badge has just been achieved, triggers a badge dialog via [badgeState].
     *
     * @param profile Current user profile.
     */
    private fun checkAndShowNewBadges(profile: UserProfile) {
        val baseBadges = defaultBadges(
            profile.customCount,
            profile.generatedCount,
            profile.totalCount,
            profile.activeDays
        )

        val oldBadgesByName = profile.badges.associateBy { it.name }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = dateFormat.format(Date())

        val freshBadges = baseBadges.map { newBadge ->
            val old = oldBadgesByName[newBadge.name]
            val justAchieved = (old == null || !old.achieved) && newBadge.achieved

            if (justAchieved) {
                newBadge.copy(
                    isNew = true,
                    achievedDate = now
                )
            } else {
                newBadge.copy(
                    isNew = old?.isNew ?: false,
                    achievedDate = old?.achievedDate
                )
            }
        }

        val newlyAchievedBadge = freshBadges.firstOrNull { it.achieved && it.isNew }
        if (newlyAchievedBadge != null && !_badgeState.value.showDialog) {
            _badgeState.value = BadgeState(
                showDialog = true,
                badgeName = newlyAchievedBadge.name,
                badgeDescription = newlyAchievedBadge.description
            )
        }

        if (profile.badges != freshBadges) {
            val uid = currentUid ?: return
            firestore.collection("users").document(uid).update("badges", freshBadges)
        }
    }

    /**
     * Saves the user profile to Firestore.
     *
     * Automatically recalculates badges, updates profile metadata and
     * checks for newly achieved badges after saving.
     *
     * @param userProfile Profile data to persist.
     */
    fun saveUserProfile(userProfile: UserProfile) {
        val uid = currentUid ?: return

        val freshBadges = defaultBadges(
            userProfile.customCount,
            userProfile.generatedCount,
            userProfile.totalCount,
            userProfile.activeDays
        )

        val withBadges = userProfile.copy(
            uid = uid,
            email = currentEmail,
            badges = freshBadges
        )

        _profileState.value = ProfileState.Loading

        firestore.collection("users").document(uid).set(withBadges)
            .addOnSuccessListener {
                _profileState.value = ProfileState.Success(withBadges)
                checkAndShowNewBadges(withBadges)
            }
            .addOnFailureListener { e ->
                _profileState.value =
                    ProfileState.Error(e.localizedMessage ?: "Błąd zapisu profilu")
            }
    }

    /**
     * Loads the user profile from Firestore.
     *
     * If the profile does not exist, creates an empty profile with default values.
     */
    fun loadUserProfile() {
        val uid = currentUid ?: return

        _profileState.value = ProfileState.Loading

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val profile = doc.toObject(UserProfile::class.java)
                if (profile != null) {
                    _profileState.value = ProfileState.Success(profile)
                    checkAndShowNewBadges(profile)
                } else {
                    val emptyProfile = UserProfile(uid = uid, email = currentEmail)
                    _profileState.value = ProfileState.Success(emptyProfile)
                }
            }
            .addOnFailureListener { e ->
                _profileState.value =
                    ProfileState.Error(e.localizedMessage ?: "Błąd pobierania profilu")
            }
    }

    /**
     * Marks a badge as seen and clears the badge dialog state.
     *
     * @param badgeName Name of the badge acknowledged by the user.
     */
    fun markBadgeAsSeen(badgeName: String) {
        _badgeState.value = BadgeState()

        val uid = currentUid ?: return
        val userRef = firestore.collection("users").document(uid)

        userRef.get().addOnSuccessListener { doc ->
            val badges = doc.get("badges") as? List<Map<String, Any>>
                ?: return@addOnSuccessListener

            val updatedBadges = badges.map { badgeMap ->
                if (badgeMap["name"] == badgeName) {
                    badgeMap.toMutableMap().apply { put("isNew", false) }
                } else {
                    badgeMap
                }
            }

            userRef.update("badges", updatedBadges)
        }
    }
}
