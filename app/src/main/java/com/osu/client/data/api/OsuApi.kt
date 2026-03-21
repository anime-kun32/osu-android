package com.osu.client.data.api

import com.osu.client.data.model.*
import retrofit2.http.*

// ── Auth (hits Cloudflare Worker directly) ────────────────────────────────────

interface AuthApi {
    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun getToken(
        @Field("client_id")     clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code")          code: String,
        @Field("grant_type")    grantType: String = "authorization_code",
        @Field("redirect_uri")  redirectUri: String,
    ): TokenResponse

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun refreshToken(
        @Field("client_id")     clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type")    grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
    ): TokenResponse

    @DELETE("oauth/tokens/current")
    suspend fun revokeToken()
}

// ── osu! API v2 ───────────────────────────────────────────────────────────────

interface OsuApi {

    // Me
    @GET("me")
    suspend fun getMe(): UserExtended

    @GET("me/{mode}")
    suspend fun getMeForMode(@Path("mode") mode: String): UserExtended

    // Users
    @GET("users/{user}")
    suspend fun getUser(
        @Path("user") userId: Long,
        @Query("mode") mode: String? = null,
    ): UserExtended

    @GET("users/{user}/scores/{type}")
    suspend fun getUserScores(
        @Path("user") userId: Long,
        @Path("type") type: String,
        @Query("mode") mode: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("include_fails") includeFails: Int? = null,
    ): List<Score>

    @GET("users/{user}/beatmapsets/{type}")
    suspend fun getUserBeatmapsets(
        @Path("user") userId: Long,
        @Path("type") type: String, // favourite, ranked, loved, graveyard, pending, guest
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
    ): List<BeatmapSet>

    // Friends
    @GET("friends")
    suspend fun getFriends(): List<UserExtended>

    // Beatmaps
    @GET("beatmapsets/search")
    suspend fun searchBeatmapsets(
        @Query("q") query: String? = null,
        @Query("mode") mode: Int? = null,
        @Query("status") status: String? = null,
        @Query("sort") sort: String? = null,
        @Query("cursor_string") cursorString: String? = null,
    ): BeatmapSearchResponse

    @GET("beatmapsets/{beatmapset}")
    suspend fun getBeatmapset(@Path("beatmapset") id: Int): BeatmapSet

    @GET("beatmaps/{beatmap}/scores")
    suspend fun getBeatmapScores(
        @Path("beatmap") id: Int,
        @Query("mode") mode: String? = null,
    ): BeatmapScoresResponse

    // Rankings
    @GET("rankings/{mode}/{type}")
    suspend fun getRankings(
        @Path("mode") mode: String,
        @Path("type") type: String,
        @Query("cursor[page]") page: Int? = null,
        @Query("country") country: String? = null,
    ): RankingsResponse

    // Chat channels
    @GET("chat/channels")
    suspend fun getChatChannels(): List<ChatChannel>

    @GET("chat/channels/{channel}/messages")
    suspend fun getChannelMessages(
        @Path("channel") channelId: Int,
        @Query("limit") limit: Int? = null,
        @Query("since") since: Long? = null,
        @Query("until") until: Long? = null,
    ): List<ChatMessage>

    @POST("chat/channels/{channel}/messages")
    suspend fun sendMessage(
        @Path("channel") channelId: Int,
        @Body body: SendMessageRequest,
    ): ChatMessage

    @PUT("chat/channels/{channel}/mark-as-read/{message}")
    suspend fun markChannelRead(
        @Path("channel") channelId: Int,
        @Path("message") messageId: Long,
    )

    @POST("chat/new")
    suspend fun newPm(@Body body: NewPmRequest): NewPmResponse

    // keepChatAlive — MUST be called every <30s or osu! kicks you from channels
    @POST("chat/ack")
    suspend fun keepChatAlive(
        @Body body: Map<String, Long> = emptyMap(),
    )

    // Notifications
    @GET("notifications")
    suspend fun getNotifications(@Query("max_id") maxId: Long? = null): NotificationsResponse

    @POST("notifications/mark-read")
    suspend fun markNotificationsRead(@Body body: MarkReadRequest)

    // News
    @GET("news")
    suspend fun getNews(@Query("limit") limit: Int? = null): NewsResponse

    // Search
    @GET("search")
    suspend fun search(
        @Query("query") query: String,
        @Query("mode") mode: String? = null,
    ): SearchResponse
}

// ── Request / Response models ─────────────────────────────────────────────────

data class SendMessageRequest(
    val message: String,
    val is_action: Boolean = false,
    val uuid: String? = null,
)

data class NewPmRequest(
    val target_id: Int,
    val message: String,
    val is_action: Boolean = false,
    val uuid: String? = null,
)

data class NewPmResponse(
    val channel: ChatChannel,
    val message: ChatMessage,
    val new_channel_id: Int,
)

data class MarkReadRequest(val ids: List<Long>)

data class BeatmapSearchResponse(
    val beatmapsets: List<BeatmapSet>,
    val cursor_string: String?,
    val total: Int,
)

data class BeatmapScoresResponse(val scores: List<Score>)

data class RankingsResponse(
    val ranking: List<UserStatisticsWithUser>,
    val total: Int,
    val cursor: RankingCursor?,
)

data class UserStatisticsWithUser(
    val global_rank: Int,
    val pp: Double,
    val user: UserExtended,
)

data class RankingCursor(val page: Int?)

data class NotificationsResponse(
    val notifications: List<OsuNotification>,
    val unread_count: Int,
)

data class OsuNotification(
    val id: Long,
    val name: String,
    val created_at: String,
    val object_type: String,
    val object_id: Int,
    val is_read: Boolean,
    val details: NotificationDetails?,
)

data class NotificationDetails(
    val title: String?,
    val username: String?,
    val cover_url: String?,
)

data class NewsResponse(
    val cursor_string: String?,
    val newsPost: List<NewsPost>,
)

data class NewsPost(
    val id: Int,
    val author: String,
    val first_image: String?,
    val published_at: String,
    val slug: String,
    val title: String,
)

data class SearchResponse(
    val users: SearchUsers?,
)

data class SearchUsers(
    val data: List<UserExtended>,
    val total: Int,
)

data class TokenResponse(
    val access_token: String,
    val refresh_token: String?,
    val expires_in: Int,
    val token_type: String,
)
