package com.osu.client.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osu.client.data.api.OsuApi
import com.osu.client.data.model.ChatChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val isLoading: Boolean = true,
    val channels: List<ChatChannel> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val osuApi: OsuApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init { loadChannels() }

    fun loadChannels() {
        viewModelScope.launch {
            _uiState.value = ChatUiState(isLoading = true)
            try {
                val channels = osuApi.getChatChannels()
                val sorted = channels.sortedWith(
                    compareByDescending<ChatChannel> { it.type == "PM" }
                        .thenByDescending { (it.lastMessageId ?: 0L) > (it.lastReadId ?: 0L) }
                        .thenByDescending { it.lastMessageId ?: 0L }
                )
                _uiState.value = ChatUiState(isLoading = false, channels = sorted)
            } catch (e: Exception) {
                _uiState.value = ChatUiState(isLoading = false, error = e.message)
            }
        }
    }
}
