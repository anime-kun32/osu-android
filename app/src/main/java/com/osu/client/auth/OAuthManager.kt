package com.osu.client.auth

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.osu.client.BuildConfig
import com.osu.client.data.api.OsuApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OAuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val osuApi: OsuApi,
    private val tokenManager: TokenManager,
) {
    private val _authState = MutableStateFlow(
        if (tokenManager.isLoggedIn()) AuthState.Authenticated else AuthState.Unauthenticated
    )
    val authState: StateFlow<AuthState> = _authState

    /** Opens osu! OAuth page in a Chrome Custom Tab. No server needed — Android catches osu://callback */
    fun launchOAuthFlow(context: Context) {
        val authUrl = Uri.parse("https://osu.ppy.sh/oauth/authorize").buildUpon()
            .appendQueryParameter("client_id", BuildConfig.OSU_CLIENT_ID)
            .appendQueryParameter("redirect_uri", BuildConfig.OSU_REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", "identify public friends.read chat.read chat.write")
            .build()

        CustomTabsIntent.Builder()
            .setShowTitle(false)
            .build()
            .launchUrl(context, authUrl)
    }

    /** Call this from MainActivity when the osu://callback deep link is received */
    suspend fun handleCallback(uri: Uri): Result<Unit> {
        val code = uri.getQueryParameter("code")
            ?: return Result.failure(Exception("No authorization code in callback"))

        return try {
            val token = osuApi.getToken(
                clientId = BuildConfig.OSU_CLIENT_ID,
                clientSecret = BuildConfig.OSU_CLIENT_SECRET,
                code = code,
                redirectUri = BuildConfig.OSU_REDIRECT_URI,
            )
            tokenManager.saveTokens(token.accessToken, token.refreshToken, token.expiresIn)
            _authState.value = AuthState.Authenticated
            Result.success(Unit)
        } catch (e: Exception) {
            _authState.value = AuthState.Unauthenticated
            Result.failure(e)
        }
    }

    suspend fun refreshIfNeeded(): Boolean {
        if (!tokenManager.isTokenExpired()) return true
        val refresh = tokenManager.refreshToken ?: run {
            logout()
            return false
        }
        return try {
            val token = osuApi.refreshToken(
                clientId = BuildConfig.OSU_CLIENT_ID,
                clientSecret = BuildConfig.OSU_CLIENT_SECRET,
                refreshToken = refresh,
                redirectUri = BuildConfig.OSU_REDIRECT_URI,
            )
            tokenManager.saveTokens(token.accessToken, token.refreshToken, token.expiresIn)
            true
        } catch (e: Exception) {
            logout()
            false
        }
    }

    suspend fun logout() {
        try { osuApi.revokeToken() } catch (_: Exception) {}
        tokenManager.clearTokens()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Authenticated : AuthState()
}
