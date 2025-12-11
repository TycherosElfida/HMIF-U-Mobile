package com.example.hmifu_mobile.feature.admin.president.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.CandidateEntity
import com.example.hmifu_mobile.data.repository.ElectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ElectionUiState(
    val candidates: List<CandidateEntity> = emptyList(),
    val hasVoted: Boolean = false,
    val votedCandidateId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isAdminMode: Boolean = false // If true, can add candidates
)

@HiltViewModel
class ElectionViewModel @Inject constructor(
    private val electionRepository: ElectionRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _messages = MutableStateFlow<Pair<String?, String?>>(null to null)

    val uiState: StateFlow<ElectionUiState> = combine(
        electionRepository.getCandidates(),
        electionRepository.getMyVote(),
        _isLoading,
        _messages
    ) { candidates, voteRecord, isLoading, messages ->
        ElectionUiState(
            candidates = candidates,
            hasVoted = voteRecord != null,
            votedCandidateId = voteRecord?.votedCandidateId,
            isLoading = isLoading,
            errorMessage = messages.first,
            successMessage = messages.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ElectionUiState()
    )

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            electionRepository.syncCandidates()
            electionRepository.checkRemoteVoteStatus()
            _isLoading.value = false
        }
    }

    fun castVote(candidateId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            electionRepository.vote(candidateId)
                .onSuccess {
                    _messages.value = null to "Vote cast successfully!"
                    electionRepository.syncCandidates() // Refresh counts
                }
                .onFailure {
                    _messages.value = (it.message ?: "Failed to vote") to null
                }
            _isLoading.value = false
        }
    }

    fun addCandidate(name: String, number: Int, vision: String, mission: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val candidate = CandidateEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                number = number,
                vision = vision,
                mission = mission
            )
            electionRepository.addCandidate(candidate)
                .onSuccess {
                    _messages.value = null to "Candidate added!"
                }
                .onFailure {
                    _messages.value = (it.message ?: "Failed to add candidate") to null
                }
            _isLoading.value = false
        }
    }

    fun clearMessages() {
        _messages.value = null to null
    }
}
