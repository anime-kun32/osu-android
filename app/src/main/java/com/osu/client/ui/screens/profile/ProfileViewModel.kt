package com.osu.client.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osu.client.data.model.Score
import com.osu.client.data.model.UserExtended
import com.osu.client.data.repository.UserRepository
import com.osu.client.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserExtended? = null,
    val bestScores: List<Score> = emptyList(),
    val recentScores: List<Score> = emptyList(),
    val firstPlaces: List<Score> = emptyList(),
    val selectedTab: ProfileTab = ProfileTab.Best,
    val isLoading: Boolean = true,
    val error: String? = null,
)

enum class ProfileTab(val label: String) {
    Best("Best"),
    Recent("Recent"),
    Firsts("Firsts"),
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val userId: Long? = savedStateHandle.get<String>("userId")?.toLongOrNull()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val userResult = if (userId != null) {
                userRepository.getUser(userId)
            } else {
                userRepository.getMe()
            }

            when (userResult) {
                is ApiResult.Success -> {
                    val user = userResult.data
                    _uiState.value = _uiState.value.copy(user = user, isLoading = false)

                    // Load all score types in parallel
                    val bestDeferred = async { userRepository.getUserBestScores(user.id) }
                    val recentDeferred = async { userRepository.getUserRecentScores(user.id) }
                    val firstsDeferred = async { userRepository.getUserFirstPlaces(user.id) }

                    val best = bestDeferred.await()
                    val recent = recentDeferred.await()
                    val firsts = firstsDeferred.await()

                    _uiState.value = _uiState.value.copy(
                        bestScores = (best as? ApiResult.Success)?.data ?: emptyList(),
                        recentScores = (recent as? ApiResult.Success)?.data ?: emptyList(),
                        firstPlaces = (firsts as? ApiResult.Success)?.data ?: emptyList(),
                    )
                }
                is ApiResult.Error -> _uiState.value =
                    _uiState.value.copy(isLoading = false, error = userResult.message)
                else -> {}
            }
        }
    }

    fun selectTab(tab: ProfileTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}
