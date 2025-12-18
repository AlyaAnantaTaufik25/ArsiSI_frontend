package com.example.arsisi_frontend.ui.akademik

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arsisi_frontend.data.model.AkademikDocument
import com.example.arsisi_frontend.ui.theme.CreamWhite
import com.example.arsisi_frontend.ui.theme.GoldenYellow
import com.example.arsisi_frontend.ui.theme.SoftOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AkademikListScreen(
    viewModel: AkademikViewModel,
    onAddClick: () -> Unit,
    onItemClick: (String) -> Unit
) {
    val documents = viewModel.documents
    // 1. UPDATE: Menambahkan "Laporan" ke dalam list tab
    val tabs = listOf("Semua", "Administrasi", "Akademik", "Laporan", "Lainnya")

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Logic Filter
    val filteredList = documents.filter { doc ->
        val matchesSearch = doc.title.contains(searchQuery, ignoreCase = true)
        val matchesTab = if (selectedTab == 0) true else doc.category.equals(tabs[selectedTab], ignoreCase = true)
        matchesSearch && matchesTab
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dokumen Akademik", color = Color.White, fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SoftOrange)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = SoftOrange,
                modifier = Modifier.offset(y = (-20).dp)
            ) {
                Icon(Icons.Filled.Add, "Tambah", tint = Color.White)
            }
        },
        containerColor = CreamWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            // --- Search Bar ---
            Row(modifier = Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Cari Dokumen...", fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) }
                )
            }

            // --- Tabs Kategori (Scrollable) ---
            // 2. UPDATE: Menggunakan Row dengan horizontalScroll agar muat banyak kategori
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .horizontalScroll(rememberScrollState()), // Agar bisa digeser ke samping
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val selected = index == selectedTab
                    Box(
                        modifier = Modifier
                            // Hapus .weight(1f) agar lebar menyesuaikan teks
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) SoftOrange else Color(0xFFEFEFEF))
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp, horizontal = 20.dp), // Padding kiri-kanan lebih besar
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (selected) Color.White else Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Text(
                text = "Daftar Dokumen (${filteredList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            // --- TAMPILAN KOSONG VS LIST ---
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Belum ada dokumen",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Silakan tambah dokumen baru melalui tombol (+)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredList) { doc ->
                        DokumenItemCard(document = doc, onClick = { onItemClick(doc.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun DokumenItemCard(document: AkademikDocument, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(GoldenYellow.copy(alpha=0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Description, null, tint = SoftOrange)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = document.category, color = SoftOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = " • ${document.date}", color = Color.Gray, fontSize = 11.sp)
                }
            }
            Icon(Icons.Filled.ChevronRight, "Detail", tint = Color.LightGray)
        }
    }
}