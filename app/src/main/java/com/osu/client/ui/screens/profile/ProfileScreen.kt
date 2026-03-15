package com.osu.client.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.osu.client.data.model.UserExtended
import com.osu.client.ui.screens.home.ScoreCard
import com.osu.client.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.user?.username ?: "Profile",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = OsuPink) }

            uiState.error != null -> Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.ErrorOutline, null, tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadProfile() }) { Text("Retry") }
                }
            }

            else -> {
                val user = uiState.user ?: return@Scaffold
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    item { ProfileHeader(user = user) }
                    item {
                        user.statistics?.let { stats ->
                            Spacer(Modifier.height(16.dp))
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        "Level ${stats.level.current}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        "${stats.level.progress}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = OsuPink,
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { stats.level.progress / 100f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                    color = OsuPink,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeCap = StrokeCap.Round,
                                )
                            }
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                    item { ProfileStatsGrid(user = user) }
                    item { Spacer(Modifier.height(16.dp)) }
                    user.statistics?.gradeCounts?.let { grades ->
                        item {
                            GradeBreakdownCard(
                                ssh = grades.ssh, ss = grades.ss,
                                sh = grades.sh, s = grades.s, a = grades.a,
                            )
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                    item {
                        val tabs = ProfileTab.values()
                        val selectedTab = uiState.selectedTab
                        PrimaryTabRow(
                            selectedTabIndex = tabs.indexOf(selectedTab),
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = OsuPink,
                        ) {
                            tabs.forEach { tab ->
                                Tab(
                                    selected = selectedTab == tab,
                                    onClick = { viewModel.selectTab(tab) },
                                    text = { Text(tab.label, fontWeight = FontWeight.SemiBold) },
                                )
                            }
                        }
                    }
                    val scores = when (uiState.selectedTab) {
                        ProfileTab.Best -> uiState.bestScores
                        ProfileTab.Recent -> uiState.recentScores
                        ProfileTab.Firsts -> uiState.firstPlaces
                    }
                    if (scores.isEmpty()) {
                        item {
                            Box(
                                Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center,
                            ) { Text("No scores yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    } else {
                        items(scores) { score ->
                            Spacer(Modifier.height(8.dp))
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                ScoreCard(score = score)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(user: UserExtended) {
    Box(modifier = Modifier.fillMaxWidth()) {
        val coverUrl = user.cover?.url ?: user.coverUrl
        if (coverUrl != null) {
            AsyncImage(
                model = coverUrl,
                contentDescription = "Cover",
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                            startY = 80f,
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF2A0A1E), Color(0xFF1A0A2E)))
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = if (coverUrl != null) 120.dp else 60.dp),
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(14.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.username,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        if (user.isSupporter) {
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.Filled.Favorite, null, tint = OsuPink, modifier = Modifier.size(18.dp))
                        }
                    }
                    user.title?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = OsuPink)
                    }
                    Text(
                        text = user.country?.name ?: user.countryCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            user.bio?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            val groups = user.groups
            if (!groups.isNullOrEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    groups.take(4).forEach { group ->
                        val chipColor = group.colour?.let { parseColor(it) } ?: OsuPink
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    group.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = chipColor,
                                )
                            },
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = chipColor.copy(alpha = 0.5f),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatsGrid(user: UserExtended) {
    val stats = user.statistics ?: return
    val fmt = { n: Int -> NumberFormat.getNumberInstance(Locale.US).format(n) }
    val fmtL = { n: Long -> NumberFormat.getNumberInstance(Locale.US).format(n) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Statistics", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("PP", "${fmt(stats.pp.toInt())}pp", OsuPink)
                StatItem("Global Rank", "#${stats.globalRank?.let { fmt(it) } ?: "-"}", OsuPurple)
                StatItem("Country Rank", "#${stats.countryRank?.let { fmt(it) } ?: "-"}", Color(0xFF66DDFF))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Accuracy", "${"%.2f".format(stats.hitAccuracy)}%")
                StatItem("Play Count", fmt(stats.playCount))
                StatItem(
                    "Ranked Score",
                    when {
                        stats.rankedScore >= 1_000_000_000L -> "${"%.1f".format(stats.rankedScore / 1e9)}B"
                        stats.rankedScore >= 1_000_000L -> "${"%.1f".format(stats.rankedScore / 1e6)}M"
                        else -> fmtL(stats.rankedScore)
                    }
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("Max Combo", "${fmt(stats.maximumCombo)}x")
                StatItem(
                    "Total Hits",
                    when {
                        stats.totalHits >= 1_000_000L -> "${"%.1f".format(stats.totalHits / 1e6)}M"
                        else -> fmtL(stats.totalHits)
                    }
                )
                StatItem("Replays Seen", fmt(stats.replaysWatchedByOthers))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = valueColor)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun GradeBreakdownCard(ssh: Int, ss: Int, sh: Int, s: Int, a: Int) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Grade Breakdown", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                GradeItem("XH", ssh, GradeSSH)
                GradeItem("X", ss, GradeSS)
                GradeItem("SH", sh, GradeSH)
                GradeItem("S", s, GradeS)
                GradeItem("A", a, GradeA)
            }
        }
    }
}

@Composable
private fun GradeItem(grade: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(grade, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = color)
        }
        Text(
            text = NumberFormat.getNumberInstance(Locale.US).format(count),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun parseColor(hex: String): Color = try {
    val cleaned = hex.trimStart('#')
    Color(android.graphics.Color.parseColor("#$cleaned"))
} catch (_: Exception) {
    OsuPink
}
