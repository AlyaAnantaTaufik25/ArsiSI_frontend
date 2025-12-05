package com.example.arsisi_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset   // <-- PAKAI INI
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.arsisi_frontend.ui.navigation.AppNavGraph
import com.example.arsisi_frontend.ui.navigation.Route
import com.example.arsisi_frontend.ui.theme.Arsisi_frontendTheme
import com.example.arsisi_frontend.ui.theme.PrimaryOrange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Arsisi_frontendTheme {
                AplikasiArsisi()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AplikasiArsisi() {
    val navController = rememberNavController()
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = {
            ArsisiBottomBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    when (index) {
                        0 -> navController.navigate(Route.LIST_TUGAS)
                        1 -> { /* TODO: route Explore */ }
                        2 -> { /* TODO: route Profile */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ArsisiBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)   // dinaikkan sedikit
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(onClick = { onItemSelected(0) }) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (selectedIndex == 0) PrimaryOrange else Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(y = (-6).dp)   // geser naik tapi tidak keluar bar
                        .size(56.dp)
                        .background(
                            color = PrimaryOrange.copy(alpha = 0.25f),
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { onItemSelected(1) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Explore",
                            tint = PrimaryOrange
                        )
                    }
                }

                IconButton(onClick = { onItemSelected(2) }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = if (selectedIndex == 2) PrimaryOrange else Color.Gray
                    )
                }
            }
        }
    }
}
