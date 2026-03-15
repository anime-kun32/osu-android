package com.osu.client.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.osu.client.data.model.Score
import com.osu.client.data.model.UserExtended
import com.osu.client.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: (Long) -> Unit,
    onNavigateToMyProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "osu!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = OsuPink,
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToMyProfile) {
                        Icon(Icons.Outlined.AccountCircle, contentDescription = "Profile")
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Outlined.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = OsuPink)
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.ErrorOutline, contentDescription = null,
                            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadHome() }) { Text("Retry") }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    uiState.user?.let { user ->
                        item { UserSummaryCard(user = user, onClick = { onNavigateToProfile(user.id) }) }
                        item { StatsRow(user = user) }
                        if (uiState.recentScores.isNotEmpty()) {
                            item {
                                Text(
                                    "Recent Scores",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            items(uiState.recentScores) { score ->
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
fun UserSummaryCard(user: UserExtended, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Box {
            // Cover image
            val coverUrl = user.cover?.url ?: user.coverUrl
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = "Cover",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop,
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surfaceVariant),
                                startY = 40f,
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .then(if (coverUrl != null) Modifier.padding(top = 60.dp) else Modifier),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "${user.username} avatar",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outline),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.username,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (user.isSupporter) {
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = "Supporter",
                                    tint = OsuPink,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🌐 ${user.countryCode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (user.isOnline) {
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF44EE88), CircleShape)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Online",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF44EE88),
                                )
                            }
                        }
                    }
                }

                val stats = user.statistics
                if (stats != null) {
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        StatChip(label = "PP", value = "${stats.pp.toInt():,}pp")
                        StatChip(label = "Global", value = "#${stats.globalRank?.let { formatNumber(it) } ?: "-"}")
                        StatChip(label = "Country", value = "#${stats.countryRank?.let { formatNumber(it) } ?: "-"}")
                        StatChip(label = "Acc", value = "${"%.2f".format(stats.hitAccuracy)}%")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = OsuPink,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun StatsRow(user: UserExtended) {
    val stats = user.statistics ?: return
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Grades", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                GradeCount("XH", stats.gradeCounts.ssh, GradeSSH)
                GradeCount("X", stats.gradeCounts.ss, GradeSS)
                GradeCount("SH", stats.gradeCounts.sh, GradeSH)
                GradeCount("S", stats.gradeCounts.s, GradeS)
                GradeCount("A", stats.gradeCounts.a, GradeA)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                InfoItem("Play Count", formatNumber(stats.playCount))
                InfoItem("Max Combo", "${formatNumber(stats.maximumCombo)}x")
                InfoItem("Level", "${stats.level.current}.${stats.level.progress}")
            }
        }
    }
}

@Composable
private fun GradeCount(grade: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = grade,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        Text(
            text = formatNumber(count),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun ScoreCard(score: Score) {
    val rankColor = when (score.rank) {
        "XH", "X" -> GradeSS
        "SH", "S" -> GradeS
        "A" -> GradeA
        "B" -> GradeB
        "C" -> GradeC
        else -> GradeD
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Beatmap thumbnail
            val coverUrl = score.beatmapset?.covers?.list
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.outline),
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = score.beatmapset?.title ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
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
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${"%.2f".format(score.accuracy * 100)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (score.mods.isNotEmpty()) {
                        Text(
                            text = "+${score.mods.joinToString("")}",
                            style = MaterialTheme.typography.labelSmall,
                            color = OsuPurple,
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = score.rank,
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

private fun formatNumber(n: Int): String =
    NumberFormat.getNumberInstance(Locale.US).format(n)
