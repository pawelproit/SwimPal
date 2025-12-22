package com.example.swimpal.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
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

    private fun handleAuthError(exception: Exception?): String {
        if (exception is FirebaseAuthException) {
            return when (exception.errorCode) {      // <- kluczowe
                "ERROR_INVALID_CUSTOM_TOKEN",
                "ERROR_CUSTOM_TOKEN_MISMATCH",
                "ERROR_INVALID_CREDENTIAL" ->
                    "Nieprawidłowe dane logowania"

                "ERROR_INVALID_EMAIL" ->
                    "Nieprawidłowy format adresu email"

                "ERROR_USER_DISABLED" ->
                    "Konto zostało zablokowane"

                "ERROR_USER_NOT_FOUND" ->
                    "Konto o podanym emailu nie istnieje"

                "ERROR_WRONG_PASSWORD" ->
                    "Błędne hasło"

                "ERROR_EMAIL_ALREADY_IN_USE" ->
                    "Podany email jest już zajęty"

                "ERROR_WEAK_PASSWORD" ->
                    "Hasło jest zbyt słabe (min. 6 znaków)"

                "ERROR_TOO_MANY_REQUESTS" ->
                    "Zbyt wiele prób. Spróbuj ponownie później"

                else ->
                    "Wystąpił błąd: ${exception.errorCode}"
            }
        }
        return "Wystąpił błąd autoryzacji"
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Zalogowano pomyślnie")
                } else {
                    _authState.value =
                        AuthState.Error(handleAuthError(task.exception))
                }
            }
    }

    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Zarejestrowano pomyślnie")
                } else {
                    _authState.value =
                        AuthState.Error(handleAuthError(task.exception))
                }
            }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
