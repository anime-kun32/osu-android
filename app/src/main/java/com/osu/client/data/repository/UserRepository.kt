package com.osu.client.data.repository

import com.osu.client.data.api.OsuApi
import com.osu.client.data.model.*
import com.osu.client.util.ApiResult
import com.osu.client.util.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: OsuApi,
) {
    suspend fun getMe(): ApiResult<UserExtended> = safeApiCall { api.getMe() }

    suspend fun getUser(userId: Long, mode: String = "osu"): ApiResult<UserExtended> =
        safeApiCall { api.getUser(userId, mode) }

    suspend fun getUserBestScores(userId: Long, limit: Int = 10, offset: Int = 0): ApiResult<List<Score>> =
        safeApiCall { api.getUserScores(userId, "best", limit = limit, offset = offset) }

    suspend fun getUserRecentScores(userId: Long, limit: Int = 10): ApiResult<List<Score>> =
        safeApiCall { api.getUserScores(userId, "recent", limit = limit, includeFails = 1) }

    suspend fun getUserFirstPlaces(userId: Long, limit: Int = 10, offset: Int = 0): ApiResult<List<Score>> =
        safeApiCall { api.getUserScores(userId, "firsts", limit = limit, offset = offset) }

    suspend fun getFriends(): ApiResult<List<UserCompact>> = safeApiCall { api.getFriends() }

    suspend fun getUserRecentActivity(userId: Long): ApiResult<List<Event>> =
        safeApiCall { api.getUserRecentActivity(userId) }
}
