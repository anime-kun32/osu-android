package com.osu.client.ui.screens.chat

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.osu.client.data.model.ChatChannel
import com.osu.client.ui.screens.home.PulseLoader
import com.osu.client.ui.screens.home.SectionLabel
import com.osu.client.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onOpenChannel: (Int, String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface0)
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(listOf(OsuPink.copy(alpha = 0.07f), Color.Transparent)),
                    CircleShape
                )
        )

        Column(Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Text(
                    "Messages",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            when {
                uiState.isLoading -> PulseLoader()
                uiState.channels.isEmpty() -> EmptyChatState(onRefresh = { viewModel.loadChannels() })
                else -> ChannelList(
                    channels = uiState.channels,
                    onClick  = onOpenChannel,
                )
            }
        }
    }
}

@Composable
private fun ChannelList(channels: List<ChatChannel>, onClick: (Int, String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp),
    ) {
        val pms = channels.filter { it.type == "PM" }
        val public = channels.filter { it.type != "PM" }

        if (pms.isNotEmpty()) {
            item { SectionLabel(title = "Direct Messages", count = pms.size) }
            itemsIndexed(pms) { index, channel ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(index * 50L)
                    visible = true
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(280)) + slideInHorizontally(tween(280)) { -20 },
                ) {
                    ChannelRow(channel = channel, onClick = { onClick(channel.channelId, channel.name) })
                }
            }
        }

        if (public.isNotEmpty()) {
            item { SectionLabel(title = "Channels", count = public.size) }
            itemsIndexed(public) { index, channel ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(index * 50L)
                    visible = true
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(280)) + slideInHorizontally(tween(280)) { -20 },
                ) {
                    ChannelRow(channel = channel, onClick = { onClick(channel.channelId, channel.name) })
                }
            }
        }
    }
}

@Composable
private fun ChannelRow(channel: ChatChannel, onClick: () -> Unit) {
    val hasUnread = (channel.lastMessageId ?: 0L) > (channel.lastReadId ?: 0L)

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (hasUnread) Surface2 else Surface1,
        border = if (hasUnread) BorderStroke(0.5.dp, OsuPink.copy(alpha = 0.3f)) else BorderStroke(0.5.dp, Color.White.copy(alpha = 0.04f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar / icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (channel.type == "PM") OsuPink.copy(alpha = 0.15f) else OsuPurple.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (channel.icon != null) {
                    AsyncImage(
                        model = channel.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        if (channel.type == "PM") Icons.Filled.Person else Icons.Filled.Forum,
                        contentDescription = null,
                        tint = if (channel.type == "PM") OsuPink else OsuPurple,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    channel.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val lastMsg = channel.recentMessages?.lastOrNull()?.content ?: channel.description ?: ""
                if (lastMsg.isNotBlank()) {
                    Text(
                        lastMsg,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hasUnread) MaterialTheme.colorScheme.onSurface.copy(0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            if (hasUnread) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(OsuPink, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun EmptyChatState(onRefresh: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                Icons.Outlined.Forum,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(56.dp),
            )
            Text(
                "No messages yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Start a conversation from someone's profile",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
            Spacer(Modifier.height(4.dp))
            FilledTonalButton(onClick = onRefresh) {
                Icon(Icons.Outlined.Refresh, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Refresh")
            }
        }
    }
}
