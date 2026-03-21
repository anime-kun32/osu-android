package com.osu.client.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osu.client.auth.OAuthManager
import com.osu.client.data.api.NewsPost
import com.osu.client.data.api.OsuApi
import com.osu.client.data.model.Score
import com.osu.client.data.model.UserExtended
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val user: UserExtended? = null,
    val onlineFriends: List<UserExtended> = emptyList(),
    val recentScores: List<Score> = emptyList(),
    val news: List<NewsPost> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val osuApi: OsuApi,
    private val oAuthManager: OAuthManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadHome() }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            try {
                // Parallel fetch everything
                val userDeferred   = async { osuApi.getMe() }
                val newsDeferred   = async { runCatching { osuApi.getNews(limit = 8) }.getOrNull() }
                val friendsDeferred = async { runCatching { osuApi.getFriends() }.getOrNull() }

                val user = userDeferred.await()
                val news = newsDeferred.await()
                val friends = friendsDeferred.await()

                val scores = runCatching {
                    osuApi.getUserScores(userId = user.id.toLong(), type = "recent", limit = 10, includeFails = 0)
                }.getOrElse { emptyList() }

                val onlineFriends = friends?.filter { it.isOnline } ?: emptyList()

                _uiState.value = HomeUiState(
                    isLoading      = false,
                    user           = user,
                    onlineFriends  = onlineFriends,
                    recentScores   = scores,
                    news           = news?.newsPost ?: emptyList(),
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState(isLoading = false, error = e.message ?: "Something went wrong")
            }
        }
    }

    fun logout() {
        viewModelScope.launch { oAuthManager.logout() }
    }
}
