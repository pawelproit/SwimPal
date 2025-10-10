package com.example.swimpal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.swimpal.viewmodel.AuthState
import androidx.compose.ui.Alignment


@Composable
fun RegisterScreen(
    authState: AuthState,
    onRegister: (String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Potwierdź hasło") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError
        )
        if (passwordError) {
            Text(
                text = "Hasła nie są takie same",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(Modifier.height(24.dp))

        when(authState) {
            is AuthState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            else -> Button(
                onClick = {
                    passwordError = password != confirmPassword
                    if (!passwordError) {
                        onRegister(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zarejestruj się")
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Masz już konto? Zaloguj się")
        }

        if (authState is AuthState.Error) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = authState.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
