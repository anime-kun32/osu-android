package com.osu.client.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.osu.client.ui.screens.home.HomeScreen
import com.osu.client.ui.screens.login.LoginScreen
import com.osu.client.ui.screens.profile.ProfileScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Screen.Login.route) {
            LoginScreen()
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.Profile.createRoute(userId))
                },
                onNavigateToMyProfile = {
                    navController.navigate(Screen.MyProfile.route)
                },
            )
        }

        composable(Screen.MyProfile.route) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            ),
        ) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
    }
}
