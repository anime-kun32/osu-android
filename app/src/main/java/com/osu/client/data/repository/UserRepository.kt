package com.osu.client.data.repository

import com.osu.client.data.api.OsuApi
import com.osu.client.data.model.Score
import com.osu.client.data.model.UserExtended
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val osuApi: OsuApi,
) {
    suspend fun getMe(): UserExtended = osuApi.getMe()

    suspend fun getUser(userId: Long): UserExtended = osuApi.getUser(userId)

    suspend fun getUserBestScores(userId: Long, limit: Int = 50): List<Score> =
        osuApi.getUserScores(userId, "best", limit = limit)

    suspend fun getUserRecentScores(userId: Long, limit: Int = 20): List<Score> =
        osuApi.getUserScores(userId, "recent", limit = limit, includeFails = 0)

    suspend fun getUserFirstPlaces(userId: Long, limit: Int = 50): List<Score> =
        osuApi.getUserScores(userId, "firsts", limit = limit)

    suspend fun getFriends(): List<UserExtended> = osuApi.getFriends()
}
