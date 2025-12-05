package com.example.arsisi_frontend.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arsisi_frontend.R
import com.example.arsisi_frontend.ui.theme.* import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var nim by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // SINKRONISASI: Menggunakan 'authState' yang diekspos oleh ViewModel
    val authState by viewModel.authState.collectAsState()

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Handle login state changes
    LaunchedEffect(authState) {
        if (authState.isSuccess) {
            onNavigateToDashboard()
            viewModel.resetState()
        }
        if (authState.error != null) {
            errorMessage = authState.error ?: "Login gagal. Cek kembali kredensial Anda."
            showError = true
            viewModel.resetState() // Reset error state di ViewModel
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        // Logo (Asumsi R.drawable.logo_arsisi tersedia)
        // Gunakan Image jika Anda punya ID drawable, atau Box/Text sebagai placeholder
        // Jika R.drawable.logo_arsisi error, ganti dengan:
        // Text(text = "LOGO", style = MaterialTheme.typography.headlineLarge)
        Image(
            painter = painterResource(id = R.drawable.logo_arsisi),
            contentDescription = "Logo ArsiSI",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Judul "Masuk"
        Text(
            text = "Masuk",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input NIM
        OutlinedTextField(
            value = nim,
            onValueChange = {
                nim = it
                showError = false // Reset error saat mengetik
            },
            label = { Text("NIM") },
            // ... (Style dan Warna) ...
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

        // Input Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false // Reset error saat mengetik
            },
            label = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            // ... (Trailing Icon) ...
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange500,
                unfocusedBorderColor = Gray400,
                focusedLabelColor = Orange500,
                unfocusedLabelColor = Gray600,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Checkbox "show password"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = showPassword,
                onCheckedChange = { showPassword = it },
                colors = CheckboxDefaults.colors(checkedColor = Orange500)
            )
            Text(
                text = "show password",
                style = MaterialTheme.typography.bodySmall,
                color = Gray600 // Menggunakan Gray600 sesuai desain
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Error Message Tampilan
        if (showError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Jarak disesuaikan

        // Tombol "Masuk"
        Button(
            onClick = {
                if (nim.isBlank() || password.isBlank()) {
                    errorMessage = "NIM dan Password harus diisi."
                    showError = true
                } else {
                    viewModel.login(nim, password)
                    showError = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange500),
            shape = RoundedCornerShape(28.dp),
            // SINKRONISASI: Akses authState.isLoading
            enabled = !authState.isLoading
        ) {
            // SINKRONISASI: Akses authState.isLoading
            if (authState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Masuk", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Teks "Belum punya akun? Daftar"
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Belum punya akun? ", style = MaterialTheme.typography.bodyMedium, color = Gray600)
            Text(
                text = "Daftar",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Orange500,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }

        // Spacer untuk mendorong footer ke bawah
        Spacer(modifier = Modifier.weight(1f))

        // Footer "Privacy Policy & Terms of Services"
        Text(
            text = "by logging in, you agree to the\nPrivacy Policy & Terms of Services",
            style = MaterialTheme.typography.bodySmall,
            color = Gray500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}