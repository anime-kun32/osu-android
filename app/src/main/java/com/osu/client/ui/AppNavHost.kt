package com.osu.client.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.compose.*
import com.osu.client.ui.screens.chat.ChatDetailScreen
import com.osu.client.ui.screens.chat.ChatScreen
import com.osu.client.ui.screens.home.HomeScreen
import com.osu.client.ui.screens.login.LoginScreen
import com.osu.client.ui.screens.profile.ProfileScreen
import com.osu.client.ui.theme.OsuPink
import com.osu.client.ui.theme.Surface0
import com.osu.client.ui.theme.Surface1
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Login      : Screen("login")
    object Home       : Screen("home")
    object Chat       : Screen("chat")
    object ChatDetail : Screen("chat_detail/{channelId}/{channelName}") {
        fun go(channelId: Int, channelName: String) =
            "chat_detail/$channelId/${URLEncoder.encode(channelName, "UTF-8")}"
    }
    object Profile    : Screen("profile/{userId}") {
        fun go(userId: Long) = "profile/$userId"
    }
    object MyProfile  : Screen("my_profile")
}

private data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

private val bottomNavItems = listOf(
    NavItem(Screen.Home,      "Home",    Icons.Outlined.Home,          Icons.Filled.Home),
    NavItem(Screen.Chat,      "Chat",    Icons.Outlined.Forum,         Icons.Filled.Forum),
    NavItem(Screen.MyProfile, "Profile", Icons.Outlined.AccountCircle, Icons.Filled.AccountCircle),
)

@Composable
fun AppNavHost(startDestination: String) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val rootRoutes = bottomNavItems.map { it.screen.route }.toSet()
    val showBottomBar = currentRoute in rootRoutes

    Scaffold(
        containerColor = Surface0,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(tween(280)) { it } + fadeIn(tween(280)),
                exit  = slideOutVertically(tween(220)) { it } + fadeOut(tween(220)),
            ) {
                NavigationBar(
                    containerColor    = Surface1,
                    contentColor      = OsuPink,
                    tonalElevation    = androidx.compose.ui.unit.Dp.Unspecified,
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick  = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.icon,
                                    contentDescription = item.label,
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = OsuPink,
                                selectedTextColor   = OsuPink,
                                indicatorColor      = OsuPink.copy(alpha = 0.14f),
                                unselectedIconColor = Color(0xFF55555E),
                                unselectedTextColor = Color(0xFF55555E),
                            ),
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(padding),
            enterTransition  = { fadeIn(tween(240)) + slideInHorizontally(tween(240)) { it / 12 } },
            exitTransition   = { fadeOut(tween(180)) },
            popEnterTransition  = { fadeIn(tween(240)) + slideInHorizontally(tween(240)) { -it / 12 } },
            popExitTransition   = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 12 } },
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToProfile = { navController.navigate(Screen.Profile.go(it)) },
                    onNavigateToDm = { id, name ->
                        navController.navigate(Screen.ChatDetail.go(id, name))
                    }
                )
            }

            composable(Screen.Chat.route) {
                ChatScreen(
                    onOpenChannel = { id, name ->
                        navController.navigate(Screen.ChatDetail.go(id, name))
                    }
                )
            }

            composable(Screen.ChatDetail.route) { back ->
                val channelId   = back.arguments?.getString("channelId")?.toIntOrNull() ?: return@composable
                val channelName = URLDecoder.decode(back.arguments?.getString("channelName") ?: "", "UTF-8")
                ChatDetailScreen(
                    channelId   = channelId,
                    channelName = channelName,
                    onBack      = { navController.popBackStack() },
                )
            }

            composable(Screen.Profile.route) { back ->
                val userId = back.arguments?.getString("userId")?.toLongOrNull() ?: return@composable
                ProfileScreen(userId = userId, onBack = { navController.popBackStack() })
            }

            composable(Screen.MyProfile.route) {
                ProfileScreen(userId = null, onBack = { navController.popBackStack() })
            }
        }
    }
}
