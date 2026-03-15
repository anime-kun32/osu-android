package com.osu.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.osu.client.auth.AuthState
import com.osu.client.auth.OAuthManager
import com.osu.client.ui.AppNavHost
import com.osu.client.ui.Screen
import com.osu.client.ui.theme.OsuClientTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var oAuthManager: OAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle OAuth deep link that may have launched this activity
        intent?.data?.let { uri ->
            if (uri.scheme == "osu" && uri.host == "callback") {
                lifecycleScope.launch {
                    oAuthManager.handleCallback(uri)
                }
            }
        }

        setContent {
            OsuClientTheme {
                val authState by oAuthManager.authState.collectAsState()
                val navController = rememberNavController()

                val startDestination = when (authState) {
                    is AuthState.Authenticated -> Screen.Home.route
                    else -> Screen.Login.route
                }

                // React to auth state changes (e.g. logout → back to login)
                LaunchedEffect(authState) {
                    when (authState) {
                        is AuthState.Authenticated -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        is AuthState.Unauthenticated -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }

                AppNavHost(
                    navController = navController,
                    startDestination = startDestination,
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        // Handle OAuth callback when app is already running (singleTop / singleTask)
        intent.data?.let { uri ->
            if (uri.scheme == "osu" && uri.host == "callback") {
                lifecycleScope.launch {
                    oAuthManager.handleCallback(uri)
                }
            }
        }
    }
}
