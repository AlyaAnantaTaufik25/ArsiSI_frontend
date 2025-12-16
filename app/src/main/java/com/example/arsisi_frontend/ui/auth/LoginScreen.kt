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
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // ================== NAVIGATION - FIXED ✅ ==================
    LaunchedEffect(authState) {
        Log.d("LoginScreen", "🔍 STATE: success=${authState.isSuccess}, error=${authState.error}")

        // SUCCESS
        if (authState.isSuccess && authState.user != null) {
            Log.d("LoginScreen", "🎯 NAV DASHBOARD!")
            onNavigateToDashboard()
        }

        // ERROR
        if (authState.error != null) {
            errorMessage = authState.error!!
            showError = true
            viewModel.resetState()
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
            onValueChange = {
                nim = it
                if (showError) showError = false
            },
            label = { Text("NIM") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange500,
                unfocusedBorderColor = Gray400,
                focusedLabelColor = Orange500,
                unfocusedLabelColor = Gray600,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ====== PASSWORD ======
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (showError) showError = false
            },
            label = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange500,
                unfocusedBorderColor = Gray400,
                focusedLabelColor = Orange500,
                unfocusedLabelColor = Gray600,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ====== ERROR MESSAGE ======
        if (showError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ====== BUTTON MASUK ======
        Button(
            onClick = {
                Log.d("LoginScreen", "🔘 LOGIN BUTTON: nim='${nim}', password='${password.length} chars'")

                if (nim.isBlank() || password.isBlank()) {
                    errorMessage = "NIM dan Password harus diisi"
                    showError = true
                } else {
                    showError = false
                    viewModel.login(nim.trim(), password.trim())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange500),
            shape = RoundedCornerShape(28.dp),
            enabled = !authState.isLoading
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

        Spacer(modifier = Modifier.height(24.dp))



        // ====== BUTTON TEST ======
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                Log.d("LoginScreen", "🧪 MANUAL NAV TEST")
                onNavigateToDashboard()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🧪 TEST: Go Dashboard")
        }


        // ====== LINK DAFTAR ======
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Belum punya akun? ",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray600
            )
            Text(
                text = "Daftar",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Orange500,
                modifier = Modifier.clickable { onNavigateToRegister() }
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
