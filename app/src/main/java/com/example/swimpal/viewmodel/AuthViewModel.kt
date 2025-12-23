package com.example.swimpal.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Represents authentication state used by the UI layer.
 *
 * - [Idle]     No authentication action is currently in progress.
 * - [Loading] Authentication request is being processed.
 * - [Success] Authentication completed successfully with an informational message.
 * - [Error]   Authentication failed with a user-readable error message.
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val error: String) : AuthState()
}

/**
 * ViewModel responsible for user authentication using Firebase Authentication.
 *
 * Exposes [authState] as a [StateFlow] to allow the UI to react to authentication
 * progress, success and errors.
 *
 * Supports email/password login, registration and logout, and maps Firebase
 * authentication errors to localized, user-friendly messages.
 */
class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)

    /**
     * Public authentication state observed by the UI.
     */
    val authState: StateFlow<AuthState> = _authState

    /**
     * Maps Firebase authentication exceptions to user-friendly error messages.
     *
     * @param exception Exception returned by Firebase authentication.
     * @return Localized error message safe to display in UI.
     */
    private fun handleAuthError(exception: Exception?): String {
        if (exception is FirebaseAuthException) {
            return when (exception.errorCode) {
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

                "ERROR_NETWORK_REQUEST_FAILED" ->
                    "Brak połączenia z internetem"

                else ->
                    "Wystąpił błąd: ${exception.errorCode}"
            }
        }
        return "Wystąpił błąd autoryzacji"
    }

    /**
     * Attempts to authenticate the user using email and password.
     *
     * Updates [authState] to:
     * - [AuthState.Loading] when the request starts
     * - [AuthState.Success] on successful login
     * - [AuthState.Error] when authentication fails
     *
     * @param email User email address.
     * @param password User password.
     */
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

    /**
     * Registers a new user using email and password.
     *
     * Updates [authState] to reflect loading, success or error states.
     *
     * @param email New user's email address.
     * @param password New user's password.
     */
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

    /**
     * Signs out the currently authenticated user and resets [authState] to [AuthState.Idle].
     */
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    /**
     * Checks whether a user is currently authenticated.
     *
     * @return `true` if a Firebase user session exists, `false` otherwise.
     */
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
