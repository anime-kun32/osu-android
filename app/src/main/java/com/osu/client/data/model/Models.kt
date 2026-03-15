package com.osu.client.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── OAuth ──────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class OAuthTokenResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String?,
    @Json(name = "expires_in") val expiresIn: Long,
    @Json(name = "token_type") val tokenType: String,
)

// ── User ───────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class UserCompact(
    val id: Long,
    val username: String,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "country_code") val countryCode: String,
    @Json(name = "is_online") val isOnline: Boolean = false,
    @Json(name = "is_supporter") val isSupporter: Boolean = false,
    @Json(name = "has_supported") val hasSupported: Boolean = false,
    @Json(name = "default_group") val defaultGroup: String? = null,
    val groups: List<UserGroup>? = null,
)

@JsonClass(generateAdapter = true)
data class UserGroup(
    val id: Int,
    val identifier: String,
    val name: String,
    val colour: String? = null,
)

@JsonClass(generateAdapter = true)
data class UserExtended(
    val id: Long,
    val username: String,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "cover_url") val coverUrl: String? = null,
    val cover: Cover? = null,
    @Json(name = "country_code") val countryCode: String,
    val country: Country? = null,
    @Json(name = "is_online") val isOnline: Boolean = false,
    @Json(name = "is_supporter") val isSupporter: Boolean = false,
    @Json(name = "has_supported") val hasSupported: Boolean = false,
    val bio: String? = null,
    val interests: String? = null,
    val location: String? = null,
    val occupation: String? = null,
    val title: String? = null,
    @Json(name = "twitter") val twitter: String? = null,
    val website: String? = null,
    @Json(name = "join_date") val joinDate: String,
    @Json(name = "last_visit") val lastVisit: String? = null,
    @Json(name = "playmode") val playmode: String,
    val statistics: UserStatistics? = null,
    @Json(name = "statistics_rulesets") val statisticsRulesets: StatisticsRulesets? = null,
    @Json(name = "rank_history") val rankHistory: RankHistory? = null,
    @Json(name = "user_achievements") val achievements: List<UserAchievement>? = null,
    @Json(name = "follower_count") val followerCount: Int = 0,
    @Json(name = "mapping_follower_count") val mappingFollowerCount: Int = 0,
    @Json(name = "post_count") val postCount: Int = 0,
    @Json(name = "scores_first_count") val scoresFirstCount: Int = 0,
    @Json(name = "scores_best_count") val scoresBestCount: Int = 0,
    @Json(name = "scores_recent_count") val scoresRecentCount: Int = 0,
    @Json(name = "beatmap_playcounts_count") val beatmapPlaycountsCount: Int = 0,
    @Json(name = "favourite_beatmapset_count") val favouriteBeatmapsetCount: Int = 0,
    @Json(name = "graveyard_beatmapset_count") val graveyardBeatmapsetCount: Int = 0,
    @Json(name = "loved_beatmapset_count") val lovedBeatmapsetCount: Int = 0,
    @Json(name = "ranked_beatmapset_count") val rankedBeatmapsetCount: Int = 0,
    @Json(name = "pending_beatmapset_count") val pendingBeatmapsetCount: Int = 0,
    val badges: List<UserBadge>? = null,
    @Json(name = "monthly_playcounts") val monthlyPlaycounts: List<MonthlyPlaycount>? = null,
    val groups: List<UserGroup>? = null,
    @Json(name = "is_bot") val isBot: Boolean = false,
    @Json(name = "is_active") val isActive: Boolean = true,
    @Json(name = "is_deleted") val isDeleted: Boolean = false,
    @Json(name = "pm_friends_only") val pmFriendsOnly: Boolean = false,
)

@JsonClass(generateAdapter = true)
data class Cover(
    val url: String?,
    @Json(name = "custom_url") val customUrl: String? = null,
    val id: String? = null,
)

@JsonClass(generateAdapter = true)
data class Country(
    val code: String,
    val name: String,
)

@JsonClass(generateAdapter = true)
data class UserStatistics(
    @Json(name = "level") val level: UserLevel,
    @Json(name = "global_rank") val globalRank: Int? = null,
    @Json(name = "country_rank") val countryRank: Int? = null,
    val pp: Double = 0.0,
    @Json(name = "ranked_score") val rankedScore: Long = 0,
    @Json(name = "total_score") val totalScore: Long = 0,
    @Json(name = "hit_accuracy") val hitAccuracy: Double = 0.0,
    @Json(name = "play_count") val playCount: Int = 0,
    @Json(name = "play_time") val playTime: Long? = null,
    @Json(name = "total_hits") val totalHits: Long = 0,
    @Json(name = "maximum_combo") val maximumCombo: Int = 0,
    @Json(name = "replays_watched_by_others") val replaysWatchedByOthers: Int = 0,
    @Json(name = "is_ranked") val isRanked: Boolean = true,
    @Json(name = "grade_counts") val gradeCounts: GradeCounts,
    @Json(name = "rank") val rank: UserRank? = null,
)

@JsonClass(generateAdapter = true)
data class UserLevel(
    val current: Int,
    val progress: Int,
)

@JsonClass(generateAdapter = true)
data class GradeCounts(
    val ss: Int = 0,
    val ssh: Int = 0,
    val s: Int = 0,
    val sh: Int = 0,
    val a: Int = 0,
)

@JsonClass(generateAdapter = true)
data class UserRank(
    @Json(name = "global") val global: Int? = null,
    val country: Int? = null,
)

@JsonClass(generateAdapter = true)
data class StatisticsRulesets(
    val osu: UserStatistics? = null,
    val taiko: UserStatistics? = null,
    @Json(name = "fruits") val fruits: UserStatistics? = null,
    val mania: UserStatistics? = null,
)

@JsonClass(generateAdapter = true)
data class RankHistory(
    val mode: String,
    val data: List<Int>,
)

@JsonClass(generateAdapter = true)
data class UserAchievement(
    @Json(name = "achievement_id") val achievementId: Int,
    @Json(name = "achieved_at") val achievedAt: String,
)

@JsonClass(generateAdapter = true)
data class UserBadge(
    @Json(name = "awarded_at") val awardedAt: String,
    val description: String,
    @Json(name = "image_url") val imageUrl: String,
    val url: String,
)

@JsonClass(generateAdapter = true)
data class MonthlyPlaycount(
    @Json(name = "start_date") val startDate: String,
    val count: Int,
)

// ── Score ──────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class Score(
    val id: Long,
    @Json(name = "best_id") val bestId: Long? = null,
    @Json(name = "user_id") val userId: Long,
    val accuracy: Double,
    val mods: List<String>,
    val score: Long,
    @Json(name = "max_combo") val maxCombo: Int,
    val perfect: Boolean = false,
    val pp: Double? = null,
    val rank: String,
    val replay: Boolean = false,
    @Json(name = "created_at") val createdAt: String,
    val beatmap: BeatmapCompact? = null,
    val beatmapset: BeatmapsetCompact? = null,
    val user: UserCompact? = null,
    val statistics: HitStatistics,
    val weight: ScoreWeight? = null,
)

@JsonClass(generateAdapter = true)
data class HitStatistics(
    val count_50: Int = 0,
    val count_100: Int = 0,
    val count_300: Int = 0,
    val count_geki: Int? = null,
    val count_katu: Int? = null,
    val count_miss: Int = 0,
)

@JsonClass(generateAdapter = true)
data class ScoreWeight(
    val percentage: Double,
    val pp: Double,
)

// ── Beatmap ────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class BeatmapCompact(
    val id: Long,
    @Json(name = "beatmapset_id") val beatmapsetId: Long,
    val mode: String,
    val status: String,
    val version: String,
    val difficulty_rating: Double = 0.0,
    val total_length: Int = 0,
    val bpm: Double? = null,
    val cs: Double = 0.0,
    val ar: Double = 0.0,
    val accuracy: Double = 0.0,
    val drain: Double = 0.0,
)

@JsonClass(generateAdapter = true)
data class BeatmapsetCompact(
    val id: Long,
    val artist: String,
    @Json(name = "artist_unicode") val artistUnicode: String? = null,
    val title: String,
    @Json(name = "title_unicode") val titleUnicode: String? = null,
    val creator: String,
    @Json(name = "user_id") val userId: Long,
    val status: String,
    @Json(name = "covers") val covers: BeatmapCovers,
    @Json(name = "play_count") val playCount: Int = 0,
    @Json(name = "favourite_count") val favouriteCount: Int = 0,
    val preview_url: String? = null,
    val bpm: Double? = null,
)

@JsonClass(generateAdapter = true)
data class Beatmapset(
    val id: Long,
    val artist: String,
    @Json(name = "artist_unicode") val artistUnicode: String? = null,
    val title: String,
    @Json(name = "title_unicode") val titleUnicode: String? = null,
    val creator: String,
    @Json(name = "user_id") val userId: Long,
    val status: String,
    @Json(name = "covers") val covers: BeatmapCovers,
    @Json(name = "play_count") val playCount: Int = 0,
    @Json(name = "favourite_count") val favouriteCount: Int = 0,
    @Json(name = "preview_url") val previewUrl: String? = null,
    val bpm: Double? = null,
    val beatmaps: List<BeatmapCompact>? = null,
    val description: BeatmapDescription? = null,
    val tags: String? = null,
    val genre: BeatmapGenre? = null,
    val language: BeatmapLanguage? = null,
    val ratings: List<Int>? = null,
)

@JsonClass(generateAdapter = true)
data class BeatmapCovers(
    val cover: String,
    @Json(name = "cover@2x") val cover2x: String? = null,
    val card: String? = null,
    @Json(name = "card@2x") val card2x: String? = null,
    val list: String? = null,
    @Json(name = "list@2x") val list2x: String? = null,
    val slimcover: String? = null,
    @Json(name = "slimcover@2x") val slimcover2x: String? = null,
)

@JsonClass(generateAdapter = true)
data class BeatmapDescription(
    val bbcode: String? = null,
    val description: String? = null,
)

@JsonClass(generateAdapter = true)
data class BeatmapGenre(
    val id: Int,
    val name: String,
)

@JsonClass(generateAdapter = true)
data class BeatmapLanguage(
    val id: Int,
    val name: String,
)

// ── Rankings ───────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class RankingsResponse(
    val cursor: RankingCursor? = null,
    @Json(name = "ranking") val ranking: List<UserStatisticsWithUser>,
    val total: Int = 0,
)

@JsonClass(generateAdapter = true)
data class RankingCursor(
    val page: Int? = null,
)

@JsonClass(generateAdapter = true)
data class UserStatisticsWithUser(
    @Json(name = "global_rank") val globalRank: Int? = null,
    val pp: Double = 0.0,
    @Json(name = "ranked_score") val rankedScore: Long = 0,
    @Json(name = "hit_accuracy") val hitAccuracy: Double = 0.0,
    @Json(name = "play_count") val playCount: Int = 0,
    @Json(name = "grade_counts") val gradeCounts: GradeCounts,
    val user: UserCompact,
)

// ── Search ─────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class BeatmapsetSearchResponse(
    val beatmapsets: List<BeatmapsetCompact>,
    @Json(name = "cursor_string") val cursorString: String? = null,
)

// ── Notifications ──────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class NotificationBundle(
    val has_more: Boolean = false,
    val notifications: List<OsuNotification>,
    @Json(name = "notification_endpoint") val notificationEndpoint: String? = null,
)

@JsonClass(generateAdapter = true)
data class OsuNotification(
    val id: Long,
    val name: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "object_type") val objectType: String? = null,
    @Json(name = "object_id") val objectId: Long? = null,
    @Json(name = "source_user_id") val sourceUserId: Long? = null,
    @Json(name = "is_read") val isRead: Boolean = false,
    val details: NotificationDetails? = null,
)

@JsonClass(generateAdapter = true)
data class NotificationDetails(
    val username: String? = null,
    val title: String? = null,
    val content: String? = null,
    @Json(name = "cover_url") val coverUrl: String? = null,
    val type: String? = null,
)

@JsonClass(generateAdapter = true)
data class MarkReadBody(
    val ids: List<Long>,
)

// ── Events ─────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class Event(
    val id: Long,
    @Json(name = "created_at") val createdAt: String,
    val type: String,
    @Json(name = "parse_error") val parseError: Boolean = false,
)
