package com.example.arsisi_frontend.ui.auth

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
// Hapus import viewModel() jika Anda menerimanya sebagai parameter
import com.example.arsisi_frontend.R
// Pastikan import theme kustom Anda berfungsi (diasumsikan)
import com.example.arsisi_frontend.ui.theme.* @Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    // ViewModel diterima dari NavGraph, jadi jangan ada = viewModel() di sini
    viewModel: AuthViewModel
) {
    // Input State
    var nim by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var angkatan by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // State Error & Loading
    val authState by viewModel.authState.collectAsState()
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Hardcode Jurusan karena tidak ada input untuk Jurusan di form ini.
    // Jika perlu input, ganti ini dengan state yang bisa diubah (misalnya Dropdown/TextField).
//    val jurusanHardcoded = "Sistem Informasi"

    // Handle auth state (Navigasi dan Error)
    LaunchedEffect(authState) {
        if (authState.isSuccess) {
            // Setelah register sukses, navigasi ke dashboard (atau login, tergantung alur API)
            onNavigateToDashboard()
            viewModel.resetState()
        }
        if (authState.error != null) {
            errorMessage = authState.error ?: "Registrasi gagal."
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
        Spacer(modifier = Modifier.height(40.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_arsisi),
            contentDescription = "Logo ArsiSI",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Judul "Daftar"
        Text(
            text = "Daftar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- INPUT FIELDS ---
        OutlinedTextField(
            value = nim,
            onValueChange = { nim = it; showError = false },
            label = { Text("NIM") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange500)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it; showError = false },
            label = { Text("Nama Lengkap") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange500)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; showError = false },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange500)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = angkatan,
            onValueChange = {
                // Batasi input angkatan maksimal 4 karakter angka
                if (it.length <= 4) angkatan = it.filter { char -> char.isDigit() };
                showError = false
            },
            label = { Text("Angkatan") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange500)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; showError = false },
            label = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            // --- PERBAIKAN: TRAILING ICON ---
            trailingIcon = {
                val icon = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (showPassword) "Hide password" else "Show password"

                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            },
            // --- END PERBAIKAN ---
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange500)
        )
        // --- END INPUT FIELDS ---

        // Checkbox "show password"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = showPassword,
                onCheckedChange = { showPassword = it },
                colors = CheckboxDefaults.colors(checkedColor = Orange500)
            )
            // Menggunakan Text yang clickable agar lebih mudah diakses
            Text(
                text = "Tampilkan Password",
                style = MaterialTheme.typography.bodySmall,
                color = Gray600,
                modifier = Modifier.clickable { showPassword = !showPassword }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Error message
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

        Spacer(modifier = Modifier.height(16.dp))

        // Teks "Sudah punya akun? Masuk"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Sudah punya akun? ", style = MaterialTheme.typography.bodyMedium, color = Gray600)
            Text(
                text = "Masuk",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Orange500,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Register Button
        Button(
            onClick = {
                // Validasi data input
                when {
                    nim.isBlank() || nama.isBlank() || email.isBlank() || angkatan.isBlank() || password.isBlank() -> {
                        errorMessage = "Semua field harus diisi"; showError = true
                    }
                    angkatan.length != 4 || angkatan.toIntOrNull() == null -> {
                        errorMessage = "Angkatan harus 4 digit angka (cth: 2021)"; showError = true
                    }
                    !email.contains("@") || !email.contains(".") -> {
                        errorMessage = "Format email tidak valid"; showError = true
                    }
                    password.length < 6 -> {
                        errorMessage = "Password minimal 6 karakter"; showError = true
                    }
                    else -> {
                        // Panggil fungsi register di ViewModel
                        viewModel.register(nim, nama, email, angkatan, password)
                        showError = false // Hilangkan error saat registrasi diproses
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange500),
            shape = RoundedCornerShape(28.dp),
            enabled = !authState.isLoading // Menonaktifkan tombol saat loading
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Daftar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy Policy
        Text(
            text = "Dengan mendaftar, Anda menyetujui\nKebijakan Privasi & Ketentuan Layanan",
            style = MaterialTheme.typography.bodySmall,
            color = Gray500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}