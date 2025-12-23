package com.example.arsisi_frontend.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arsisi_frontend.R
import com.example.arsisi_frontend.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: AuthViewModel
) {
    // ================== STATE INPUT ==================
    var nim by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // ================== STATE AUTH ==================
    val authState by viewModel.authState.collectAsState()

    // ================== AUTO NAVIGATION ✅ ==================
    LaunchedEffect(authState) {
        Log.d("LoginScreen", "🔍 STATE: loading=${authState.isLoading}, success=${authState.isSuccess}, error=${authState.error}")

        if (authState.isSuccess && authState.user != null) {
            Log.d("LoginScreen", "🎯 AUTO NAV TO DASHBOARD")
            delay(500)  // Brief success feedback
            onNavigateToDashboard()
        }
    }

    // ================== UI ==================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_arsisi),
            contentDescription = "Logo ArsiSI",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Masuk",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ====== NIM ======
        OutlinedTextField(
            value = nim,
            onValueChange = { nim = it },
            label = { Text("NIM") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !authState.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange500,
                unfocusedBorderColor = Gray400,
                focusedLabelColor = Orange500,
                unfocusedLabelColor = Gray600,
                disabledBorderColor = Gray400,
                disabledLabelColor = Gray600
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ====== PASSWORD ======
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }, enabled = !authState.isLoading) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility",
                        tint = if (authState.isLoading) Gray400 else Orange500
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !authState.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange500,
                unfocusedBorderColor = Gray400,
                focusedLabelColor = Orange500,
                unfocusedLabelColor = Gray600,
                disabledBorderColor = Gray400,
                disabledLabelColor = Gray600
            )
        )

        // ====== SERVER ERROR ✅ ==================
        authState.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ====== BUTTON MASUK ✅ ==================
        Button(
            onClick = {
                if (nim.isBlank()) {
                    Log.w("LoginScreen", "⚠️ NIM kosong")
                } else if (password.isBlank()) {
                    Log.w("LoginScreen", "⚠️ Password kosong")
                } else if (!authState.isLoading) {
                    Log.d("LoginScreen", "🔘 LOGIN: nim=${nim.trim()}")
                    viewModel.login(nim.trim(), password.trim())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (authState.isLoading) Gray400 else Orange500
            ),
            shape = RoundedCornerShape(28.dp),
            enabled = nim.isNotBlank() && password.isNotBlank() && !authState.isLoading
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Masuk",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ====== LINK DAFTAR ======
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Belum punya akun? ",
                style = MaterialTheme.typography.bodyMedium,
                color = if (authState.isLoading) Gray400 else Gray600
            )
            Text(
                text = "Daftar",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (authState.isLoading) Gray400 else Orange500,
                modifier = Modifier.clickable(enabled = !authState.isLoading) {
                    onNavigateToRegister()
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "by logging in, you agree to the\nPrivacy Policy & Terms of Services",
            style = MaterialTheme.typography.bodySmall,
            color = Gray500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}
