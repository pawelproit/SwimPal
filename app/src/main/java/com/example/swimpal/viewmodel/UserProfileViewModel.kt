package com.example.swimpal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swimpal.model.UserProfile
import com.example.swimpal.model.Badge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val userProfile: UserProfile) : ProfileState()
    data class Error(val error: String) : ProfileState()
}

class UserProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUid get() = auth.currentUser?.uid
    private val currentEmail get() = auth.currentUser?.email.orEmpty()
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState

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
        firestore.collection("users").document(uid)
            .set(withBadges)
            .addOnSuccessListener {
                _profileState.value = ProfileState.Success(withBadges)
            }
            .addOnFailureListener { e ->
                _profileState.value = ProfileState.Error(
                    e.localizedMessage ?: "Błąd zapisu profilu"
                )
            }
    }

    fun loadUserProfile() {
        val uid = currentUid ?: return
        _profileState.value = ProfileState.Loading
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val profile = doc.toObject(UserProfile::class.java)
                if (profile != null) {
                    val baseBadges = defaultBadges(
                        profile.customCount,
                        profile.generatedCount,
                        profile.totalCount,
                        profile.activeDays
                    )

                    val oldBadges = profile.badges.associateBy { it.name }

                    val freshBadges = baseBadges.map { newBadge ->
                        val old = oldBadges[newBadge.name]
                        val justAchieved =
                            (old == null || !old.achieved) && newBadge.achieved
                        newBadge.copy(
                            isNew = justAchieved
                        )
                    }

                    val profileWithBadges = profile.copy(badges = freshBadges)
                    _profileState.value = ProfileState.Success(profileWithBadges)

                    if (profile.badges != freshBadges) {
                        firestore.collection("users")
                            .document(uid)
                            .update("badges", freshBadges)
                    }
                } else {
                    _profileState.value = ProfileState.Success(
                        UserProfile(uid = uid, email = currentEmail)
                    )
                }
            }
            .addOnFailureListener { e ->
                _profileState.value = ProfileState.Error(
                    e.localizedMessage ?: "Błąd pobierania profilu"
                )
            }
    }

    fun markBadgeAsSeen(badgeName: String) {
        val userId = auth.currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val userRef = db.collection("users").document(userId)
        userRef.get().addOnSuccessListener { doc ->
            val badges = doc.get("badges") as? List<Map<String, Any>> ?: return@addOnSuccessListener
            val updatedBadges = badges.map { badgeMap ->
                if (badgeMap["name"] == badgeName) {
                    badgeMap.toMutableMap().apply {
                        put("isNew", false)
                    }
                } else {
                    badgeMap
                }
            }
            userRef.update("badges", updatedBadges)
        }
    }
}
