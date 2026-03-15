package com.osu.client.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: Long) = "profile/$userId"
    }
    object MyProfile : Screen("me")
    object Beatmaps : Screen("beatmaps")
    object Rankings : Screen("rankings")
}
