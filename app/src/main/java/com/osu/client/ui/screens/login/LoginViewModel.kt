package com.osu.client.ui.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import com.osu.client.auth.OAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val oAuthManager: OAuthManager,
) : ViewModel() {
    fun loginWithOsu(context: Context) {
        oAuthManager.launchOAuthFlow(context)
    }
}
