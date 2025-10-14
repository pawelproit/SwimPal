package com.example.swimpal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swimpal.model.UserProfile
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
    private val currentUid get() = FirebaseAuth.getInstance().currentUser?.uid
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState

    fun saveUserProfile(userProfile: UserProfile) {
        val uid = currentUid ?: return
        _profileState.value = ProfileState.Loading
        firestore.collection("users").document(uid)
            .set(userProfile.copy(uid = uid))
            .addOnSuccessListener {
                _profileState.value = ProfileState.Success(userProfile.copy(uid = uid))
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
                    _profileState.value = ProfileState.Success(profile)
                } else {
                    _profileState.value = ProfileState.Error("Brak danych profilu")
                }
            }
            .addOnFailureListener { e ->
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Błąd pobierania profilu")
            }
    }
}
