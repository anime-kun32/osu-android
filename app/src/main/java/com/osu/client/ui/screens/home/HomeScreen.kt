package com.osu.client.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.osu.client.data.model.Score
import com.osu.client.data.model.UserExtended
import com.osu.client.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToProfile: (Long) -> Unit,
    onNavigateToDm: (Int, String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface0)
    ) {
        // Ambient top-left glow
        Box(
            modifier = Modifier
                .size(380.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .background(
                    Brush.radialGradient(listOf(OsuPink.copy(alpha = 0.09f), Color.Transparent)),
                    CircleShape
                )
        )
        // Ambient bottom-right glow
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .background(
                    Brush.radialGradient(listOf(OsuPurple.copy(alpha = 0.07f), Color.Transparent)),
                    CircleShape
                )
        )

        when {
            uiState.isLoading -> PulseLoader()
            uiState.error != null -> ErrorState(message = uiState.error!!, onRetry = { viewModel.loadHome() })
            else -> HomeContent(uiState = uiState, onNavigateToProfile = onNavigateToProfile, onNavigateToDm = onNavigateToDm)
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onNavigateToProfile: (Long) -> Unit,
    onNavigateToDm: (Int, String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        uiState.user?.let { user ->
            item { HeroCard(user = user, onClick = { onNavigateToProfile(user.id) }) }
            item { Spacer(Modifier.height(4.dp)) }
            item { QuickStatsRow(user = user) }
        }

        if (uiState.onlineFriends.isNotEmpty()) {
            item { SectionLabel(title = "Online Now", count = uiState.onlineFriends.size) }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(uiState.onlineFriends) { index, friend ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(index * 60L)
                            visible = true
                        }
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.85f),
                        ) {
                            OnlineFriendChip(
                                user = friend,
                                onClick = { onNavigateToDm(friend.id.toInt(), friend.username) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }

        if (uiState.recentScores.isNotEmpty()) {
            item { SectionLabel(title = "Recent Plays") }
            itemsIndexed(uiState.recentScores) { index, score ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(index * 70L)
                    visible = true
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -24 },
                ) {
                    ScoreRow(score = score)
                }
            }
        }

        if (uiState.news.isNotEmpty()) {
            item { SectionLabel(title = "osu! News") }
            items(uiState.news) { post ->
                NewsCard(post = post)
            }
        }
    }
}

// ── Hero Card ─────────────────────────────────────────────────────────────────

@Composable
private fun HeroCard(user: UserExtended, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(2200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow_alpha",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = user.cover?.url ?: user.coverUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        // Scrim gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.15f),
                            0.45f to Color.Black.copy(alpha = 0.55f),
                            1.0f to Surface0,
                        )
                    )
                )
        )
        // Pink glow line at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, OsuPink.copy(alpha = glowAlpha), Color.Transparent)
                    )
                )
        )

        // User info
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar with pink ring
            Box {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(listOf(OsuPink, OsuPurple, OsuPink)),
                            shape = CircleShape,
                        )
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Surface2),
                    contentScale = ContentScale.Crop,
                )
                // Online indicator
                if (user.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color(0xFF00DD88), CircleShape)
                            .border(2.dp, Surface0, CircleShape)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (user.isSupporter) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = OsuPink,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
                user.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = OsuPink,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = user.country?.name ?: user.countryCode,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                )
            }
        }
    }
}

// ── Quick stats row ───────────────────────────────────────────────────────────

@Composable
private fun QuickStatsRow(user: UserExtended) {
    val stats = user.statistics ?: return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickStatCard(modifier = Modifier.weight(1f), label = "Performance", value = "${fmt(stats.pp.toInt())}pp", color = OsuPink)
        QuickStatCard(modifier = Modifier.weight(1f), label = "Global", value = "#${fmtRank(stats.globalRank)}", color = OsuPurple)
        QuickStatCard(modifier = Modifier.weight(1f), label = "Accuracy", value = "${"%.2f".format(stats.hitAccuracy)}%", color = OsuBlue)
    }
}

@Composable
private fun QuickStatCard(modifier: Modifier, label: String, value: String, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Surface2,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ── Online friends ─────────────────────────────────────────────────────────────

@Composable
private fun OnlineFriendChip(user: UserExtended, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Surface2,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Surface3),
                    contentScale = ContentScale.Crop,
                )
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .align(Alignment.BottomEnd)
                        .background(Color(0xFF00DD88), CircleShape)
                        .border(2.dp, Surface2, CircleShape)
                )
            }
            Text(
                text = user.username,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 60.dp),
            )
        }
    }
}

// ── Score row ─────────────────────────────────────────────────────────────────

@Composable
fun ScoreRow(score: Score) {
    val rankLabel = when (score.rank) {
        "XH"  -> "SS+"
        "X"   -> "SS"
        "SH"  -> "S+"
        "S"   -> "S"
        "A"   -> "A"
        "B"   -> "B"
        "C"   -> "C"
        else  -> "D"
    }
    val rankColor = when (score.rank) {
        "XH", "X"  -> GradeSS
        "SH", "S"  -> GradeS
        "A"         -> GradeA
        "B"         -> GradeB
        "C"         -> GradeC
        else        -> GradeD
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = Surface1,
        tonalElevation = 0.dp,
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Beatmap cover
            AsyncImage(
                model = score.beatmapset?.covers?.list,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface3),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(12.dp))

            // Title + meta
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = score.beatmapset?.title ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = score.beatmapset?.artist ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${"%.2f".format(score.accuracy * 100)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (score.mods.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = OsuPurple.copy(alpha = 0.18f),
                        ) {
                            Text(
                                text = score.mods.joinToString(""),
                                style = MaterialTheme.typography.labelSmall,
                                color = OsuPurple,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // Rank + PP
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = rankLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = rankColor,
                )
                score.pp?.let {
                    Text(
                        text = "${it.toInt()}pp",
                        style = MaterialTheme.typography.labelMedium,
                        color = OsuPink,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

// ── News card ─────────────────────────────────────────────────────────────────

@Composable
private fun NewsCard(post: com.osu.client.data.api.NewsPost) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        color = Surface1,
        tonalElevation = 0.dp,
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            // Pink left accent bar
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(listOf(OsuPink, OsuPurple)),
                    )
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = post.published_at.take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
fun SectionLabel(title: String, count: Int? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .background(OsuPink, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        count?.let {
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = OsuPink.copy(alpha = 0.18f),
            ) {
                Text(
                    text = "$it",
                    style = MaterialTheme.typography.labelSmall,
                    color = OsuPink,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                )
            }
        }
    }
}

// ── Loading + Error ───────────────────────────────────────────────────────────

@Composable
fun PulseLoader() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "alpha",
    )
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = OsuPink.copy(alpha = alpha),
                modifier = Modifier.size(36.dp),
                strokeWidth = 2.dp,
            )
            Spacer(Modifier.height(14.dp))
            Text("loading...", color = Color.White.copy(alpha = alpha * 0.5f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.ErrorOutline, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            FilledTonalButton(onClick = onRetry) { Text("Retry") }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun fmt(n: Int): String = NumberFormat.getNumberInstance(Locale.US).format(n)
private fun fmtRank(rank: Int?): String = rank?.let { NumberFormat.getNumberInstance(Locale.US).format(it) } ?: "-"
