package com.osu.client.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserExtended(
    val id: Int,
    val username: String,
    @Json(name = "avatar_url")   val avatarUrl: String,
    @Json(name = "cover_url")    val coverUrl: String?,
    @Json(name = "country_code") val countryCode: String,
    @Json(name = "is_active")    val isActive: Boolean = true,
    @Json(name = "is_bot")       val isBot: Boolean = false,
    @Json(name = "is_online")    val isOnline: Boolean = false,
    @Json(name = "is_supporter") val isSupporter: Boolean = false,
    @Json(name = "last_visit")   val lastVisit: String? = null,
    @Json(name = "pm_friends_only") val pmFriendsOnly: Boolean = false,
    @Json(name = "profile_colour")  val profileColour: String? = null,
    val statistics: UserStatistics? = null,
    val playmode: String? = null,
    val groups: List<UserGroup>? = null,
    val title: String? = null,
    @Json(name = "title_url") val titleUrl: String? = null,
    val country: Country? = null,
    val cover: Cover? = null,
    val badges: List<Badge>? = null,
    @Json(name = "rank_history") val rankHistory: RankHistory? = null,
    @Json(name = "follower_count") val followerCount: Int? = null,
    val bio: String? = null,
)

@JsonClass(generateAdapter = true)
data class UserStatistics(
    @Json(name = "grade_counts") val gradeCounts: GradeCounts,
    val level: Level,
    @Json(name = "global_rank")    val globalRank: Int?,
    @Json(name = "country_rank")   val countryRank: Int?,
    val pp: Double,
    @Json(name = "ranked_score")   val rankedScore: Long,
    @Json(name = "hit_accuracy")   val hitAccuracy: Double,
    @Json(name = "play_count")     val playCount: Int,
    @Json(name = "play_time")      val playTime: Int,
    @Json(name = "total_score")    val totalScore: Long,
    @Json(name = "total_hits")     val totalHits: Long,
    @Json(name = "maximum_combo")  val maximumCombo: Int,
    @Json(name = "replays_watched_by_others") val replaysWatchedByOthers: Int = 0,
)

@JsonClass(generateAdapter = true)
data class GradeCounts(val ss: Int, val ssh: Int, val s: Int, val sh: Int, val a: Int)

@JsonClass(generateAdapter = true)
data class Level(val current: Int, val progress: Int)

@JsonClass(generateAdapter = true)
data class UserGroup(
    val id: Int,
    val identifier: String,
    val name: String,
    val colour: String?,
    @Json(name = "short_name") val shortName: String,
)

@JsonClass(generateAdapter = true)
data class Country(val code: String, val name: String)

@JsonClass(generateAdapter = true)
data class Cover(
    val url: String?,
    @Json(name = "custom_url") val customUrl: String?,
    val id: String?,
)

@JsonClass(generateAdapter = true)
data class Badge(
    @Json(name = "awarded_at")  val awardedAt: String,
    val description: String,
    @Json(name = "image_url")   val imageUrl: String,
    val url: String,
)

@JsonClass(generateAdapter = true)
data class RankHistory(val mode: String, val data: List<Int>)

// ── Beatmap ───────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class BeatmapSet(
    val id: Int,
    val title: String,
    @Json(name = "title_unicode") val titleUnicode: String?,
    val artist: String,
    @Json(name = "artist_unicode") val artistUnicode: String?,
    val creator: String,
    @Json(name = "user_id") val userId: Int,
    val source: String = "",
    val status: String,
    @Json(name = "favourite_count") val favouriteCount: Int = 0,
    @Json(name = "play_count")      val playCount: Int = 0,
    val covers: BeatmapCovers?,
    val beatmaps: List<Beatmap>? = null,
    val bpm: Double? = null,
    @Json(name = "has_video") val hasVideo: Boolean = false,
    val ranked: Int = 0,
    val tags: String? = null,
    val nsfw: Boolean = false,
)

@JsonClass(generateAdapter = true)
data class BeatmapCovers(
    val cover: String?,
    @Json(name = "cover@2x") val cover2x: String?,
    val card: String?,
    @Json(name = "card@2x") val card2x: String?,
    val list: String?,
    @Json(name = "list@2x") val list2x: String?,
    val slimcover: String?,
    @Json(name = "slimcover@2x") val slimcover2x: String?,
)

@JsonClass(generateAdapter = true)
data class Beatmap(
    val id: Int,
    @Json(name = "beatmapset_id") val beatmapsetId: Int,
    val mode: String,
    @Json(name = "mode_int") val modeInt: Int,
    val status: String,
    val version: String,
    @Json(name = "difficulty_rating") val difficulty: Double,
    val bpm: Double?,
    val cs: Double,
    val drain: Double,
    val accuracy: Double,
    val ar: Double,
    @Json(name = "hit_length")   val hitLength: Int,
    @Json(name = "total_length") val totalLength: Int,
    @Json(name = "max_combo")    val maxCombo: Int?,
    val url: String,
)

// ── Score ─────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class Score(
    val id: Long,
    @Json(name = "user_id")   val userId: Int,
    val accuracy: Double,
    val mods: List<String>,
    val score: Long,
    @Json(name = "max_combo") val maxCombo: Int,
    val perfect: Boolean,
    val statistics: ScoreStatistics,
    val rank: String,
    @Json(name = "created_at") val createdAt: String,
    val mode: String,
    @Json(name = "mode_int") val modeInt: Int,
    val pp: Double?,
    val beatmap: Beatmap?,
    val beatmapset: BeatmapSet?,
    val user: UserExtended?,
    val replay: Boolean = false,
    val weight: ScoreWeight?,
)

@JsonClass(generateAdapter = true)
data class ScoreStatistics(
    val count_50: Int, val count_100: Int, val count_300: Int,
    val count_geki: Int, val count_katu: Int, val count_miss: Int,
)

@JsonClass(generateAdapter = true)
data class ScoreWeight(val percentage: Double, val pp: Double)

// ── Chat ──────────────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class ChatChannel(
    @Json(name = "channel_id")    val channelId: Int,
    val name: String,
    val description: String?,
    val icon: String?,
    val type: String,
    @Json(name = "message_length_limit") val messageLengthLimit: Int = 1500,
    val moderated: Boolean = false,
    @Json(name = "last_message_id") val lastMessageId: Long?,
    @Json(name = "last_read_id")    val lastReadId: Long?,
    val users: List<Int>?,
    @Json(name = "recent_messages") val recentMessages: List<ChatMessage>?,
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    @Json(name = "channel_id")  val channelId: Int,
    val content: String,
    @Json(name = "is_action")   val isAction: Boolean,
    @Json(name = "message_id")  val messageId: Long,
    @Json(name = "sender_id")   val senderId: Int,
    val timestamp: String,
    val type: String,
    val uuid: String?,
    val sender: UserExtended?,
)
