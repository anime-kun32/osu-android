package com.osu.client.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osu.client.auth.OAuthManager
import com.osu.client.data.model.Score
import com.osu.client.data.model.UserExtended
import com.osu.client.data.repository.UserRepository
import com.osu.client.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: UserExtended? = null,
    val recentScores: List<Score> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val oAuthManager: OAuthManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = userRepository.getMe()) {
                is ApiResult.Success -> {
                    val user = result.data
                    _uiState.value = _uiState.value.copy(user = user, isLoading = false)
                    // Load recent scores in parallel
                    launch {
                        when (val scores = userRepository.getUserRecentScores(user.id, limit = 5)) {
                            is ApiResult.Success -> _uiState.value =
                                _uiState.value.copy(recentScores = scores.data)
                            else -> {}
                        }
                    }
                }
                is ApiResult.Error -> _uiState.value =
                    _uiState.value.copy(isLoading = false, error = result.message)
                else -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch { oAuthManager.logout() }
    }
}
