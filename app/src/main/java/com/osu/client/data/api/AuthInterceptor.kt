package com.osu.client.data.api

import com.osu.client.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.accessToken
        val request = if (token != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
        } else {
            chain.request().newBuilder()
                .header("Accept", "application/json")
                .build()
        }
        return chain.proceed(request)
    }
}
