package com.osu.client.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osu.client.auth.TokenManager
import com.osu.client.data.api.OsuApi
import com.osu.client.data.api.SendMessageRequest
import com.osu.client.data.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject

data class ChatDetailUiState(
    val isLoading: Boolean = true,
    val isConnected: Boolean = false,
    val isSending: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val myUserId: Int = -1,
    val error: String? = null,
)

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val osuApi: OsuApi,
    private val tokenManager: TokenManager,
    private val okHttpClient: OkHttpClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    private var channelId: Int = -1
    private var lastMessageId: Long? = null
    private var webSocket: WebSocket? = null
    private var pollJob: Job? = null
    private var keepAliveJob: Job? = null

    fun init(channelId: Int) {
        if (this.channelId == channelId) return
        this.channelId = channelId
        loadInitial()
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _uiState.value = ChatDetailUiState(isLoading = true)
            try {
                val me = osuApi.getMe()
                val messages = osuApi.getChannelMessages(channelId, limit = 50)
                    .sortedBy { it.messageId }

                lastMessageId = messages.lastOrNull()?.messageId

                _uiState.value = ChatDetailUiState(
                    isLoading = false,
                    messages  = messages,
                    myUserId  = me.id,
                )

                lastMessageId?.let { osuApi.markChannelRead(channelId, it) }
                connectWebSocket()
                startKeepAlive()
            } catch (e: Exception) {
                _uiState.value = ChatDetailUiState(isLoading = false, error = e.message)
                startPollingFallback()
            }
        }
    }

    private fun connectWebSocket() {
        val token = tokenManager.accessToken ?: return
        val request = Request.Builder()
            .url("wss://notify.ppy.sh")
            .header("Authorization", "Bearer $token")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                _uiState.update { it.copy(isConnected = true) }
            }

            override fun onMessage(ws: WebSocket, text: String) {
                viewModelScope.launch { handleWebSocketMessage(text) }
            }

            override fun onMessage(ws: WebSocket, bytes: ByteString) {
                viewModelScope.launch { handleWebSocketMessage(bytes.utf8()) }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                _uiState.update { it.copy(isConnected = false) }
                startPollingFallback()
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                _uiState.update { it.copy(isConnected = false) }
                startPollingFallback()
            }
        })
    }

    private suspend fun handleWebSocketMessage(text: String) {
        try {
            val json = JSONObject(text)
            val event = json.optString("event")
            if (event == "chat.message.new" || json.has("messages")) {
                fetchNewMessages()
            }
        } catch (_: Exception) {}
    }

    private fun startKeepAlive() {
        keepAliveJob?.cancel()
        keepAliveJob = viewModelScope.launch {
            while (isActive) {
                delay(25_000L)
                try { osuApi.keepChatAlive() } catch (_: Exception) {}
            }
        }
    }

    private fun startPollingFallback() {
        if (pollJob?.isActive == true) return
        pollJob = viewModelScope.launch {
            while (isActive) {
                delay(5_000L)
                fetchNewMessages()
            }
        }
    }

    private suspend fun fetchNewMessages() {
        if (channelId == -1) return
        try {
            val newMessages = osuApi.getChannelMessages(
                channelId = channelId,
                since     = lastMessageId,
                limit     = 50,
            ).sortedBy { it.messageId }

            if (newMessages.isNotEmpty()) {
                lastMessageId = newMessages.last().messageId
                _uiState.update { state ->
                    val existing = state.messages.map { it.messageId }.toSet()
                    val combined = state.messages + newMessages.filter { it.messageId !in existing }
                    state.copy(messages = combined.sortedBy { it.messageId })
                }
                lastMessageId?.let { osuApi.markChannelRead(channelId, it) }
            }
        } catch (_: Exception) {}
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || channelId == -1) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }
            try {
                val sent = osuApi.sendMessage(
                    channelId = channelId,
                    body      = SendMessageRequest(
                        message = text,
                        uuid    = UUID.randomUUID().toString(),
                    ),
                )
                _uiState.update { state ->
                    val existing = state.messages.map { it.messageId }.toSet()
                    val updated  = if (sent.messageId !in existing) state.messages + sent else state.messages
                    state.copy(isSending = false, messages = updated.sortedBy { it.messageId })
                }
                lastMessageId = sent.messageId
                osuApi.markChannelRead(channelId, sent.messageId)
            } catch (e: Exception) {
                _uiState.update { it.copy(isSending = false, error = e.message) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.close(1000, "ViewModel cleared")
        pollJob?.cancel()
        keepAliveJob?.cancel()
    }
}