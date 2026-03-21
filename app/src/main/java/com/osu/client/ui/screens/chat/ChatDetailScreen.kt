package com.osu.client.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.osu.client.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    channelId: Int,
    channelName: String,
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboard = LocalSoftwareKeyboardController.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(channelId) { viewModel.init(channelId) }

    // Auto scroll to bottom on new messages
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface0)
            .imePadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            // ── Top bar ───────────────────────────────────────────────────────
            Surface(
                color = Surface1,
                tonalElevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(OsuPink.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Person, null, tint = OsuPink, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            channelName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (uiState.isConnected) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(6.dp).background(Color(0xFF00DD88), CircleShape))
                                Spacer(Modifier.width(4.dp))
                                Text("live", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00DD88))
                            }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            }

            // ── Messages ──────────────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = OsuPink, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                    }
                } else if (uiState.messages.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.ChatBubbleOutline, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f), modifier = Modifier.size(48.dp))
                            Text("Say something!", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        itemsIndexed(uiState.messages, key = { _, m -> m.messageId }) { index, message ->
                            val isMine = message.senderId == uiState.myUserId
                            val showName = !isMine && (index == 0 || uiState.messages[index - 1].senderId != message.senderId)

                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { 20 },
                            ) {
                                MessageBubble(
                                    content  = message.content,
                                    isMine   = isMine,
                                    sender   = if (showName) message.sender?.username else null,
                                    time     = message.timestamp.takeLast(8).take(5),
                                )
                            }
                        }
                    }
                }
            }

            // ── Input bar ─────────────────────────────────────────────────────
            Surface(color = Surface1, tonalElevation = 0.dp) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { if (it.length <= 1500) inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Message...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = OsuPink.copy(alpha = 0.6f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.3f),
                            focusedContainerColor   = Surface2,
                            unfocusedContainerColor = Surface2,
                            cursorColor          = OsuPink,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText.trim())
                                inputText = ""
                                keyboard?.hide()
                            }
                        }),
                        maxLines = 4,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    )
                    Spacer(Modifier.width(8.dp))
                    val canSend = inputText.isNotBlank() && !uiState.isSending
                    FilledIconButton(
                        onClick = {
                            if (canSend) {
                                viewModel.sendMessage(inputText.trim())
                                inputText = ""
                                keyboard?.hide()
                            }
                        },
                        enabled = canSend,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = OsuPink,
                            contentColor   = Color(0xFF1A0010),
                            disabledContainerColor = OsuPink.copy(alpha = 0.3f),
                        ),
                    ) {
                        if (uiState.isSending) {
                            CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Message Bubble ────────────────────────────────────────────────────────────

@Composable
private fun MessageBubble(
    content: String,
    isMine: Boolean,
    sender: String?,
    time: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
    ) {
        sender?.let {
            Text(
                it,
                style = MaterialTheme.typography.labelSmall,
                color = OsuPink,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
            )
        }
        Row(
            modifier = Modifier.widthIn(max = 280.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (isMine) OsuPink else Surface2,
                        shape = RoundedCornerShape(
                            topStart    = 18.dp,
                            topEnd      = 18.dp,
                            bottomStart = if (isMine) 18.dp else 4.dp,
                            bottomEnd   = if (isMine) 4.dp else 18.dp,
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(
                    content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMine) Color(0xFF1A0010) else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        Text(
            time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        )
    }
}
