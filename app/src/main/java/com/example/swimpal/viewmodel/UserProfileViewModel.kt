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

    private fun defaultBadges(
        custom: Int,
        generated: Int,
        total: Int,
        days: Int
    ): List<Badge> = listOf(
        Badge("Custom 5", "5 treningów własnych", custom >= 5),
        Badge("Custom 10", "10 treningów własnych", custom >= 10),
        Badge("Generated 5", "5 treningów generowanych", generated >= 5),
        Badge("Generated 10", "10 treningów generowanych", generated >= 10),
        Badge("Total 20", "20 treningów łącznie", total >= 20),
        Badge("Total 50", "50 treningów łącznie", total >= 50),
        Badge("Days 5", "5 aktywnych dni", days >= 5),
        Badge("Days 20", "20 aktywnych dni", days >= 20)
    )

    // Zawsze zapisujemy najnowszy stan badgy!
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
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Błąd zapisu profilu")
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
                    val freshBadges = defaultBadges(
                        profile.customCount,
                        profile.generatedCount,
                        profile.totalCount,
                        profile.activeDays
                    )
                    // Zawsze nadpisujemy badge zaktualizowanymi achieved!
                    val profileWithBadges = profile.copy(badges = freshBadges)
                    _profileState.value = ProfileState.Success(profileWithBadges)
                    // Aktualizuj w Firebase jeśli są rozbieżności
                    if (profile.badges != freshBadges) {
                        firestore.collection("users").document(uid).update("badges", freshBadges)
                    }
                } else {
                    _profileState.value = ProfileState.Success(
                        UserProfile(uid = uid, email = currentEmail)
                    )
                }
            }
            .addOnFailureListener { e ->
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Błąd pobierania profilu")
            }
    }
}
