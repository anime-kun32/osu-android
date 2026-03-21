package com.osu.client.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.osu.client.data.model.BeatmapSet
import com.osu.client.data.model.Score
import com.osu.client.data.model.UserExtended
import com.osu.client.ui.screens.home.ErrorState
import com.osu.client.ui.screens.home.PulseLoader
import com.osu.client.ui.screens.home.ScoreRow
import com.osu.client.ui.screens.home.SectionLabel
import com.osu.client.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long?,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) { viewModel.load(userId) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface0)
    ) {
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(listOf(OsuPurple.copy(alpha = 0.07f), Color.Transparent)),
                    CircleShape
                )
        )

        when {
            uiState.isLoading -> PulseLoader()
            uiState.error != null -> ErrorState(uiState.error!!) { viewModel.load(userId) }
            uiState.user != null -> ProfileContent(
                uiState = uiState,
                onBack  = onBack,
                viewModel = viewModel,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onBack: () -> Unit,
    viewModel: ProfileViewModel,
) {
    val user = uiState.user!!

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        // ── Cover + Avatar ──────────────────────────────────────────────────
        item {
            ProfileHero(user = user, onBack = onBack)
        }

        // ── Quick stats ─────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(4.dp))
            ProfileStatsRow(user = user)
        }

        // ── Level progress ──────────────────────────────────────────────────
        user.statistics?.let { stats ->
            item {
                Spacer(Modifier.height(12.dp))
                LevelProgress(
                    level    = stats.level.current,
                    progress = stats.level.progress,
                )
            }
        }

        // ── Grade breakdown ─────────────────────────────────────────────────
        user.statistics?.gradeCounts?.let { grades ->
            item {
                Spacer(Modifier.height(8.dp))
                GradeBreakdownRow(grades = grades)
            }
        }

        // ── Rank history graph ──────────────────────────────────────────────
        val rankHistory = uiState.user.rankHistory?.data
        if (!rankHistory.isNullOrEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                SectionLabel(title = "Rank History")
                RankHistoryGraph(data = rankHistory)
            }
        }

        // ── Score tabs ──────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(8.dp))
            val tabs = ProfileTab.values()
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOf(uiState.selectedTab),
                containerColor   = Surface0,
                contentColor     = OsuPink,
                edgePadding      = 16.dp,
                indicator        = { tabPositions ->
                    val index = tabs.indexOf(uiState.selectedTab)
                    if (index < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[index])
                                .padding(horizontal = 16.dp),
                            color = OsuPink,
                        )
                    }
                },
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) },
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick  = { viewModel.selectTab(tab) },
                        text     = {
                            Text(
                                tab.label,
                                fontWeight = if (uiState.selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        selectedContentColor   = OsuPink,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // ── Content by tab ──────────────────────────────────────────────────
        when (uiState.selectedTab) {
            ProfileTab.Best, ProfileTab.Recent, ProfileTab.Firsts -> {
                val scores = when (uiState.selectedTab) {
                    ProfileTab.Best   -> uiState.bestScores
                    ProfileTab.Recent -> uiState.recentScores
                    else              -> uiState.firstPlaces
                }
                if (scores.isEmpty()) {
                    item { EmptyTabState() }
                } else {
                    itemsIndexed(scores) { index, score ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(uiState.selectedTab) {
                            kotlinx.coroutines.delay(index * 55L)
                            visible = true
                        }
                        AnimatedVisibility(
                            visible = visible,
                            enter   = fadeIn(tween(280)) + slideInHorizontally(tween(280)) { -20 },
                        ) {
                            Spacer(Modifier.height(4.dp))
                            ScoreRow(score = score)
                        }
                    }
                }
            }
            ProfileTab.Favourites -> {
                if (uiState.favouriteMaps.isEmpty()) {
                    item { EmptyTabState() }
                } else {
                    itemsIndexed(uiState.favouriteMaps) { index, map ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(uiState.selectedTab) {
                            kotlinx.coroutines.delay(index * 55L)
                            visible = true
                        }
                        AnimatedVisibility(
                            visible = visible,
                            enter   = fadeIn(tween(280)) + slideInHorizontally(tween(280)) { -20 },
                        ) {
                            Spacer(Modifier.height(4.dp))
                            BeatmapRow(map = map)
                        }
                    }
                }
            }
        }
    }
}

// ── Profile Hero ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileHero(user: UserExtended, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        // Cover
        val coverUrl = user.cover?.url ?: user.coverUrl
        AsyncImage(
            model = coverUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.3f),
                            0.5f to Color.Black.copy(alpha = 0.6f),
                            1.0f to Surface0,
                        )
                    )
                )
        )

        // Glow line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(listOf(Color.Transparent, OsuPink.copy(0.6f), Color.Transparent))
                )
        )

        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape),
        ) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        // Avatar + info
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Box {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .border(
                            2.dp,
                            Brush.sweepGradient(listOf(OsuPink, OsuPurple, OsuPink)),
                            CircleShape,
                        )
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Surface2),
                    contentScale = ContentScale.Crop,
                )
                if (user.isOnline) {
                    Box(
                        Modifier
                            .size(15.dp)
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
                        user.username,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                    )
                    if (user.isSupporter) {
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Filled.Favorite, null, tint = OsuPink, modifier = Modifier.size(16.dp))
                    }
                }
                user.title?.let {
                    Text(it, style = MaterialTheme.typography.labelMedium, color = OsuPink, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    user.country?.name ?: user.countryCode,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.55f),
                )
            }
        }
    }
}

// ── Stats row ─────────────────────────────────────────────────────────────────

@Composable
private fun ProfileStatsRow(user: UserExtended) {
    val stats = user.statistics ?: return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatPill(Modifier.weight(1f), label = "PP",      value = "${fmt(stats.pp.toInt())}pp", color = OsuPink)
        StatPill(Modifier.weight(1f), label = "Global",  value = "#${fmtR(stats.globalRank)}",  color = OsuGold)
        StatPill(Modifier.weight(1f), label = "Country", value = "#${fmtR(stats.countryRank)}", color = OsuBlue)
        StatPill(Modifier.weight(1f), label = "Acc",     value = "${"%.1f".format(stats.hitAccuracy)}%", color = OsuPurple)
    }
}

@Composable
private fun StatPill(modifier: Modifier, label: String, value: String, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Surface2,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ── Level progress ────────────────────────────────────────────────────────────

@Composable
private fun LevelProgress(level: Int, progress: Int) {
    val animProgress by animateFloatAsState(
        targetValue = progress / 100f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "level_progress"
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        color = Surface2,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Level $level",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    "$progress%",
                    style = MaterialTheme.typography.labelMedium,
                    color = OsuPink,
                )
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Surface3)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animProgress)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(listOf(OsuPink, OsuPurple)),
                            CircleShape
                        )
                )
            }
        }
    }
}

// ── Grade breakdown ───────────────────────────────────────────────────────────

@Composable
private fun GradeBreakdownRow(grades: com.osu.client.data.model.GradeCounts) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = Surface2,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            GradePill("SS+", grades.ssh, GradeSSH)
            GradePill("SS",  grades.ss,  GradeSS)
            GradePill("S+",  grades.sh,  GradeSH)
            GradePill("S",   grades.s,   GradeS)
            GradePill("A",   grades.a,   GradeA)
        }
    }
}

@Composable
private fun GradePill(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = color)
        }
        Text(fmt(count), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Rank history graph ────────────────────────────────────────────────────────

@Composable
private fun RankHistoryGraph(data: List<Int>) {
    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "graph_draw",
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Surface2,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Rank Progress",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(12.dp))

            val minRank  = data.min().toFloat()
            val maxRank  = data.max().toFloat()
            val range    = (maxRank - minRank).coerceAtLeast(1f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val w = size.width
                val h = size.height
                val stepX = w / (data.size - 1).coerceAtLeast(1)
                val pointsToDraw = (data.size * animProgress).toInt().coerceAtLeast(2)

                val path = Path()
                val fillPath = Path()

                data.take(pointsToDraw).forEachIndexed { i, rank ->
                    // Invert: lower rank number = higher on chart
                    val x = i * stepX
                    val y = h * (rank - minRank) / range
                    if (i == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, h)
                        fillPath.lineTo(x, y)
                    } else {
                        // Cubic bezier for smooth curve
                        val prevX = (i - 1) * stepX
                        val prevY = h * (data[i - 1] - minRank) / range
                        val cpX = (prevX + x) / 2f
                        path.cubicTo(cpX, prevY, cpX, y, x, y)
                        fillPath.cubicTo(cpX, prevY, cpX, y, x, y)
                    }
                }

                // Fill gradient under line
                val lastIndex = pointsToDraw - 1
                val lastX = lastIndex * stepX
                fillPath.lineTo(lastX, h)
                fillPath.close()

                drawPath(
                    path  = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(OsuPink.copy(alpha = 0.25f), Color.Transparent),
                        startY = 0f,
                        endY   = h,
                    ),
                )

                // Draw line
                drawPath(
                    path   = path,
                    color  = OsuPink,
                    style  = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )

                // Dots at start and end
                if (pointsToDraw >= 2) {
                    val endX = (pointsToDraw - 1) * stepX
                    val endY = h * (data[pointsToDraw - 1] - minRank) / range
                    drawCircle(color = OsuPink, radius = 5f, center = Offset(endX, endY))
                    drawCircle(color = Surface0, radius = 3f, center = Offset(endX, endY))
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("90 days ago", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val latest = data.lastOrNull()
                Text(
                    text = latest?.let { "#${fmt(it)}" } ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = OsuPink,
                    fontWeight = FontWeight.SemiBold,
                )
                Text("now", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ── Beatmap row ───────────────────────────────────────────────────────────────

@Composable
private fun BeatmapRow(map: BeatmapSet) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = Surface1,
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.05f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = map.covers?.list ?: map.covers?.cover,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface3),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    map.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    map.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "by ${map.creator}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OsuPink.copy(alpha = 0.7f),
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OsuPink.copy(alpha = 0.15f),
                ) {
                    Text(
                        map.status.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = OsuPink,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "${fmt(map.favouriteCount)} favs",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyTabState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "nothing here yet",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun fmt(n: Int)    = NumberFormat.getNumberInstance(Locale.US).format(n)
private fun fmtR(n: Int?)  = n?.let { NumberFormat.getNumberInstance(Locale.US).format(it) } ?: "-"
