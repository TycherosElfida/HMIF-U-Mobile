package com.example.hmifu_mobile.feature.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.PollEntity
import com.example.hmifu_mobile.data.repository.PollRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PollsUiState(
    val isLoading: Boolean = false,
    val isVoting: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class PollViewModel @Inject constructor(
    private val pollRepository: PollRepository
) : ViewModel() {

    val polls: StateFlow<List<PollEntity>> = pollRepository
        .observeActivePolls()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(PollsUiState())
    val uiState: StateFlow<PollsUiState> = _uiState.asStateFlow()

    init {
        syncPolls()
    }

    private fun syncPolls() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            pollRepository.syncPolls().collect { result ->
                result.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun vote(pollId: String, optionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isVoting = true)
            pollRepository.vote(pollId, optionId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isVoting = false,
                        successMessage = "Vote submitted!"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isVoting = false,
                        error = e.message
                    )
                }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
