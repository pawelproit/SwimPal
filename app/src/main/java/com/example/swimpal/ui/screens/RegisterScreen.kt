package com.example.swimpal.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swimpal.viewmodel.AuthState

/**
 * Registration screen for creating a new SwimPal user account.
 *
 * Displays a registration form with validation for email and password fields,
 * handles loading and error states, and allows navigation to the login screen.
 * All validation and UI state are managed locally, while registration logic
 * is delegated to the authentication layer.
 *
 * @param authState Current authentication state provided by the ViewModel.
 * @param onRegister Invoked when the user submits a valid registration form.
 * @param onNavigateToLogin Navigates the user to the login screen.
 */


@Composable
fun RegisterScreen(
    authState: AuthState,
    onRegister: (String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }

    val emailError by remember(email) {
        derivedStateOf {
            if (email.isBlank()) "Email nie może być pusty"
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Nieprawidłowy email"
            else null
        }
    }
    val passwordError by remember(password) {
        derivedStateOf {
            if (password.isBlank()) "Hasło nie może być puste"
            else if (password.length < 6) "Hasło musi mieć min. 6 znaków"
            else null
        }
    }
    val confirmPasswordError by remember(password, confirmPassword) {
        derivedStateOf {
            if (confirmPassword.isBlank()) "Pole wymagane"
            else if (password != confirmPassword) "Hasła nie są takie same"
            else null
        }
    }


    val showEmailError = emailTouched && emailError != null
    val showPasswordError = passwordTouched && passwordError != null
    val showConfirmPasswordError = confirmPasswordTouched && confirmPasswordError != null

    val isFormValid by remember(emailError, passwordError, confirmPasswordError, email, password) {
        derivedStateOf {
            emailError == null &&
                    passwordError == null &&
                    confirmPasswordError == null &&
                    email.isNotBlank() &&
                    password.isNotBlank()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00BCD4),
                        Color(0xFF0288D1)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🏊",
                fontSize = 80.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Dołącz do nas!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Stwórz konto SwimPal",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (!emailTouched) emailTouched = true
                        },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        isError = showEmailError,
                        supportingText = {
                            if (showEmailError) {
                                Text(
                                    emailError ?: "",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showEmailError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            unfocusedBorderColor = if (showEmailError) {
                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (!passwordTouched) passwordTouched = true
                        },
                        label = { Text("Hasło") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Hasło",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = showPasswordError,
                        supportingText = {
                            if (showPasswordError) {
                                Text(
                                    passwordError ?: "",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showPasswordError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            unfocusedBorderColor = if (showPasswordError) {
                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            if (!confirmPasswordTouched) confirmPasswordTouched = true
                        },
                        label = { Text("Potwierdź hasło") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Potwierdź hasło",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = showConfirmPasswordError,
                        supportingText = {
                            if (showConfirmPasswordError) {
                                Text(
                                    confirmPasswordError ?: "",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showConfirmPasswordError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            unfocusedBorderColor = if (showConfirmPasswordError) {
                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    when (authState) {
                        is AuthState.Loading -> CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )

                        else -> Button(
                            onClick = {
                                emailTouched = true
                                passwordTouched = true
                                confirmPasswordTouched = true
                                if (isFormValid) {
                                    onRegister(email, password)
                                }
                            },
                            enabled = authState !is AuthState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFormValid) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                }
                            )
                        ) {
                            Text(
                                text = "Zarejestruj się",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (authState is AuthState.Error) {
                        Text(
                            text = authState.error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Masz już konto? Zaloguj się",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}
