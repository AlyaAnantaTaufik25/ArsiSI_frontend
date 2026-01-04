
package com.example.arsisi_frontend.navigation
sealed class NavigationRoute {
    object Loading : NavigationRoute()
    data class Navigate(val route: String) : NavigationRoute()
}