package com.osu.client.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osu.client.data.api.OsuApi
import com.osu.client.data.model.BeatmapSet
import com.osu.client.data.model.Score
import com.osu.client.data.model.UserExtended
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ProfileTab(val label: String) {
    Best("Top Plays"),
    Recent("Recent"),
    Firsts("Firsts"),
    Favourites("Favourites"),
}

data class ProfileUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val user: UserExtended? = null,
    val bestScores: List<Score> = emptyList(),
    val recentScores: List<Score> = emptyList(),
    val firstPlaces: List<Score> = emptyList(),
    val favouriteMaps: List<BeatmapSet> = emptyList(),
    val selectedTab: ProfileTab = ProfileTab.Best,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val osuApi: OsuApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load(userId: Long?) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState(isLoading = true)
            try {
                val user = if (userId == null) osuApi.getMe() else osuApi.getUser(userId)

                // Parallel load all score types + favourites
                val bestDeferred      = async { runCatching { osuApi.getUserScores(user.id.toLong(), "best",    limit = 50) }.getOrElse { emptyList() } }
                val recentDeferred    = async { runCatching { osuApi.getUserScores(user.id.toLong(), "recent",  limit = 20, includeFails = 0) }.getOrElse { emptyList() } }
                val firstsDeferred    = async { runCatching { osuApi.getUserScores(user.id.toLong(), "firsts",  limit = 50) }.getOrElse { emptyList() } }
                val favouriteDeferred = async { runCatching { osuApi.getUserBeatmapsets(user.id.toLong(), "favourite", limit = 50) }.getOrElse { emptyList() } }

                _uiState.value = ProfileUiState(
                    isLoading     = false,
                    user          = user,
                    bestScores    = bestDeferred.await(),
                    recentScores  = recentDeferred.await(),
                    firstPlaces   = firstsDeferred.await(),
                    favouriteMaps = favouriteDeferred.await(),
                    selectedTab   = ProfileTab.Best,
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(isLoading = false, error = e.message ?: "Failed to load profile")
            }
        }
    }

    fun selectTab(tab: ProfileTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}
