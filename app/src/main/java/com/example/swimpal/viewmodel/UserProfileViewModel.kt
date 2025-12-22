package com.example.swimpal.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.swimpal.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.swimpal.model.Badge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val userProfile: UserProfile) : ProfileState()
    data class Error(val error: String) : ProfileState()
}

data class BadgeState(
    val showDialog: Boolean = false,
    val badgeName: String = "",
    val badgeDescription: String = ""
)

class UserProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUid get() = auth.currentUser?.uid
    private val currentEmail get() = auth.currentUser?.email.orEmpty()
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _badgeState = mutableStateOf(BadgeState())
    val badgeState: State<BadgeState> = _badgeState

    private val BADGE_CUSTOM_5 = "Custom 5"
    private val BADGE_CUSTOM_10 = "Custom 10"
    private val BADGE_GENERATED_5 = "Generated 5"
    private val BADGE_GENERATED_10 = "Generated 10"
    private val BADGE_TOTAL_20 = "Total 20"
    private val BADGE_TOTAL_50 = "Total 50"
    private val BADGE_DAYS_5 = "Days 5"
    private val BADGE_DAYS_20 = "Days 20"
    private val BADGE_CUSTOM_20 = "Custom 20"
    private val BADGE_CUSTOM_50 = "Custom 50"
    private val BADGE_GENERATED_20 = "Generated 20"
    private val BADGE_GENERATED_50 = "Generated 50"
    private val BADGE_TOTAL_100 = "Total 100"
    private val BADGE_DAYS_50 = "Days 50"

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


    fun saveUserProfile(userProfile: UserProfile) {
        val uid = currentUid ?: return
        val freshBadges = defaultBadges(
            userProfile.customCount,
            userProfile.generatedCount,
            userProfile.totalCount,
            userProfile.activeDays
        )
        val withBadges = userProfile.copy(uid = uid, email = currentEmail, badges = freshBadges)
        _profileState.value = ProfileState.Loading
        firestore.collection("users").document(uid).set(withBadges)
            .addOnSuccessListener {
                _profileState.value = ProfileState.Success(withBadges)
                checkAndShowNewBadges(withBadges)
            }
            .addOnFailureListener { e ->
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Błąd zapisu profilu")
            }
    }

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
                    _profileState.value = ProfileState.Success(UserProfile(uid = uid, email = currentEmail))
                }
            }
            .addOnFailureListener { e ->
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Błąd pobierania profilu")
            }
    }

    fun markBadgeAsSeen(badgeName: String) {
        _badgeState.value = BadgeState()
        val userId = auth.currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)
        userRef.get().addOnSuccessListener { doc ->
            val badges = doc.get("badges") as? List<Map<String, Any>> ?: return@addOnSuccessListener
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
