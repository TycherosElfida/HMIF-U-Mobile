package com.example.hmifu_mobile.feature.secretariat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.DocumentEntity
import com.example.hmifu_mobile.data.local.entity.DocumentStatus
import com.example.hmifu_mobile.data.local.entity.DocumentType
import com.example.hmifu_mobile.data.repository.AuthRepository
import com.example.hmifu_mobile.data.repository.DocumentRepository
import com.example.hmifu_mobile.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SecretariatUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val documents: List<DocumentEntity> = emptyList(),
    val myDocuments: List<DocumentEntity> = emptyList(),
    val isSecretary: Boolean = false,
    val isPresident: Boolean = false,
    val isVicePresident: Boolean = false,
    
    // Form State
    val title: String = "",
    val url: String = "",
    val type: DocumentType = DocumentType.PROPOSAL
)

@HiltViewModel
class SecretariatViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecretariatUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Determine Role
                val userProfileResult = userRepository.syncCurrentUser()
                val role = userProfileResult.getOrNull()?.role?.lowercase() ?: ""
                val isSecretary = role == "secretary"
                val isPresident = role == "president"
                val isVicePresident = role == "vice_president"

                // Load Documents based on role
                val userId = authRepository.currentUser?.uid ?: ""

                // Combine flows if needed, but for now we observe all for update
                // Observing all documents for President (to approve) and own for Secretary
                
               documentRepository.getAllDocuments().collect { allDocs ->
                   _uiState.update { state ->
                       state.copy(
                           isLoading = false,
                            isSecretary = isSecretary,
                            isPresident = isPresident,
                            isVicePresident = isVicePresident,
                            documents = if (isPresident || isVicePresident) allDocs.filter { it.status == DocumentStatus.PENDING } else emptyList(),
                            myDocuments = if (isSecretary) allDocs.filter { it.uploaderId == userId } else emptyList()
                        )
                   }
               }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateUrl(url: String) {
        _uiState.update { it.copy(url = url) }
    }

    fun updateType(type: DocumentType) {
        _uiState.update { it.copy(type = type) }
    }

    fun submitDocument() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.title.isBlank() || state.url.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Title and URL are required") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            val currentUser = authRepository.currentUser
            
            val document = DocumentEntity(
                title = state.title,
                url = state.url,
                type = state.type,
                uploaderId = currentUser?.uid ?: "",
                uploaderName = currentUser?.displayName ?: "Unknown",
                status = DocumentStatus.PENDING
            )

            documentRepository.addDocument(document)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            successMessage = "Document submitted successfully",
                            title = "",
                            url = ""
                        ) 
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun updateDocumentStatus(id: String, status: DocumentStatus, rejectionReason: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            documentRepository.updateStatus(id, status, rejectionReason)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Document $status") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
