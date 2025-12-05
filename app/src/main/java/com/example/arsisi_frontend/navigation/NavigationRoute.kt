// File: com/example/arsisi_frontend/navigation/NavigationRoute.kt
package com.example.arsisi_frontend.navigation

// Status yang akan diekspos oleh ViewModel untuk navigasi awal
sealed class NavigationRoute {
    object Loading : NavigationRoute()
    data class Navigate(val route: String) : NavigationRoute()
}