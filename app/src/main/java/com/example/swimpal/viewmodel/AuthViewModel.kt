package com.example.swimpal.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Zalogowano pomyślnie")
                } else {
                    _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Błąd logowania")
                }
            }
    }

    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sukces rejestracji – przejdzie na PersonalDataScreen w nawigacji
                    _authState.value = AuthState.Success("Zarejestrowano pomyślnie")
                } else {
                    _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Błąd rejestracji")
                }
            }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
