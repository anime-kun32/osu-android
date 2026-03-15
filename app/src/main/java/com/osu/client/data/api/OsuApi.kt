package com.osu.client.data.api

import com.osu.client.data.model.*
import retrofit2.http.*

interface OsuApi {

    // ── Auth ──────────────────────────────────────────────────────────────────

    @POST("oauth/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("redirect_uri") redirectUri: String,
    ): OAuthTokenResponse

    @POST("oauth/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("redirect_uri") redirectUri: String,
    ): OAuthTokenResponse

    @DELETE("api/v2/oauth/tokens/current")
    suspend fun revokeToken()

    // ── Me / Own user ─────────────────────────────────────────────────────────

    @GET("api/v2/me/{mode}")
    suspend fun getMe(
        @Path("mode") mode: String = "osu",
    ): UserExtended

    // ── Users ─────────────────────────────────────────────────────────────────

    @GET("api/v2/users/{user}/{mode}")
    suspend fun getUser(
        @Path("user") userId: Long,
        @Path("mode") mode: String = "osu",
    ): UserExtended

    @GET("api/v2/users/{user}/scores/{type}")
    suspend fun getUserScores(
        @Path("user") userId: Long,
        @Path("type") type: String, // best | firsts | recent
        @Query("mode") mode: String = "osu",
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("include_fails") includeFails: Int = 0,
    ): List<Score>

    @GET("api/v2/users/{user}/beatmapsets/{type}")
    suspend fun getUserBeatmapsets(
        @Path("user") userId: Long,
        @Path("type") type: String, // favourite | graveyard | loved | most_played | pending | ranked
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
    ): List<BeatmapsetCompact>

    @GET("api/v2/users/{user}/recent_activity")
    suspend fun getUserRecentActivity(
        @Path("user") userId: Long,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
    ): List<Event>

    // ── Friends ───────────────────────────────────────────────────────────────

    @GET("api/v2/friends")
    suspend fun getFriends(): List<UserCompact>

    // ── Rankings ──────────────────────────────────────────────────────────────

    @GET("api/v2/rankings/{mode}/{type}")
    suspend fun getRankings(
        @Path("mode") mode: String = "osu",
        @Path("type") type: String = "performance",
        @Query("cursor[page]") page: Int = 1,
        @Query("country") country: String? = null,
    ): RankingsResponse

    // ── Beatmaps ──────────────────────────────────────────────────────────────

    @GET("api/v2/beatmapsets/search")
    suspend fun searchBeatmapsets(
        @Query("q") query: String? = null,
        @Query("mode") mode: Int? = null,
        @Query("status") status: String? = null,
        @Query("cursor_string") cursorString: String? = null,
    ): BeatmapsetSearchResponse

    @GET("api/v2/beatmapsets/{beatmapset}")
    suspend fun getBeatmapset(
        @Path("beatmapset") beatmapsetId: Long,
    ): Beatmapset

    // ── Notifications ─────────────────────────────────────────────────────────

    @GET("api/v2/notifications")
    suspend fun getNotifications(
        @Query("max_id") maxId: Long? = null,
    ): NotificationBundle

    @POST("api/v2/notifications/mark-read")
    suspend fun markNotificationsRead(
        @Body body: MarkReadBody,
    )
}
