package com.example.hmifu_mobile.feature.admin.president.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.DocumentEntity
import com.example.hmifu_mobile.data.local.entity.DocumentStatus
import com.example.hmifu_mobile.data.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DocumentInboxUiState(
    val pendingDocuments: List<DocumentEntity> = emptyList(),
    val historyDocuments: List<DocumentEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class DocumentInboxViewModel @Inject constructor(
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _messages = MutableStateFlow<Pair<String?, String?>>(null to null) // error, success

    val uiState: StateFlow<DocumentInboxUiState> = combine(
        documentRepository.getDocuments(),
        _isLoading,
        _messages
    ) { documents, isLoading, messages ->
        DocumentInboxUiState(
            pendingDocuments = documents.filter { it.status == DocumentStatus.PENDING.name },
            historyDocuments = documents.filter { it.status != DocumentStatus.PENDING.name },
            isLoading = isLoading,
            errorMessage = messages.first,
            successMessage = messages.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DocumentInboxUiState()
    )

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            // In real app, sync with Firestore here
            try {
                // documentRepository.syncDocuments() 
                // For demo, we might seed if empty?
                // documentRepository.seedDummyData("secretary_1")
            } catch (e: Exception) {
                _messages.value = e.message to null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approveDocument(id: String) {
        updateStatus(id, DocumentStatus.APPROVED.name)
    }

    fun rejectDocument(id: String) {
        updateStatus(id, DocumentStatus.REJECTED.name)
    }

    private fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                documentRepository.updateStatus(id, status).onSuccess {
                    _messages.value = null to "Document marked as $status"
                }.onFailure {
                    _messages.value = it.message to null
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Debug helper
    fun createDummyProposal() {
        viewModelScope.launch {
             documentRepository.seedDummyData("dummy_sec")
             _messages.value = null to "Dummy proposal created"
        }
    }

    fun clearMessages() {
        _messages.value = null to null
    }
}
