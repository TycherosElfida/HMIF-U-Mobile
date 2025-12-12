package com.example.hmifu_mobile.feature.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.dao.AnnouncementDao
import com.example.hmifu_mobile.data.local.entity.AnnouncementCategory
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.repository.AnnouncementRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Create Announcement UI state.
 */
data class CreateAnnouncementUiState(
    val title: String = "",
    val body: String = "",
    val category: AnnouncementCategory = AnnouncementCategory.GENERAL,
    val isPinned: Boolean = false,
    val attachmentUrl: String = "",
    val id: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean
        get() = title.isNotBlank() && body.isNotBlank()
}

/**
 * ViewModel for creating announcements.
 */
@HiltViewModel
class CreateAnnouncementViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle,
    private val announcementDao: AnnouncementDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAnnouncementUiState())
    val uiState: StateFlow<CreateAnnouncementUiState> = _uiState.asStateFlow()

    init {
        val announcementId = savedStateHandle.get<String>("announcementId")
        if (announcementId != null) {
            loadAnnouncement(announcementId)
        }
    }

    private fun loadAnnouncement(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val announcement = announcementRepository.getById(id)
            if (announcement != null) {
                _uiState.update {
                    it.copy(
                        id = announcement.id,
                        title = announcement.title,
                        body = announcement.body,
                        category = AnnouncementCategory.fromString(announcement.category),
                        isPinned = announcement.isPinned,
                        attachmentUrl = announcement.attachmentUrl ?: "",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Announcement not found") }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateBody(body: String) {
        _uiState.update { it.copy(body = body) }
    }

    fun updateCategory(category: AnnouncementCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun updatePinned(isPinned: Boolean) {
        _uiState.update { it.copy(isPinned = isPinned) }
    }

    fun updateAttachmentUrl(url: String) {
        _uiState.update { it.copy(attachmentUrl = url) }
    }

    fun saveAnnouncement() {
        if (!uiState.value.isValid) {
            _uiState.update { it.copy(errorMessage = "Please fill in all required fields") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val state = _uiState.value
                val id = state.id ?: UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val authorId = auth.currentUser?.uid ?: ""
                val attachment = state.attachmentUrl.ifBlank { null }
                
                if (state.id != null) {
                    // Update existing
                    val original = announcementRepository.getById(state.id)
                    val announcement = AnnouncementEntity(
                        id = id,
                        title = state.title,
                        body = state.body,
                        category = state.category.name,
                        isPinned = state.isPinned,
                        authorId = original?.authorId ?: authorId,
                        authorName = original?.authorName ?: "",
                        attachmentUrl = attachment,
                        createdAt = original?.createdAt ?: now,
                        updatedAt = now
                    )
                    announcementRepository.update(announcement).onFailure { throw it }
                } else {
                    // Create new
                    val announcement = AnnouncementEntity(
                        id = id,
                        title = state.title,
                        body = state.body,
                        category = state.category.name,
                        isPinned = state.isPinned,
                        authorId = authorId,
                        attachmentUrl = attachment,
                        createdAt = now,
                        updatedAt = now
                    )
                    
                    val data = mapOf(
                        "id" to id,
                        "title" to state.title,
                        "body" to state.body,
                        "category" to state.category.name,
                        "isPinned" to state.isPinned,
                        "authorId" to authorId,
                        "attachmentUrl" to attachment,
                        "createdAt" to now,
                        "updatedAt" to now
                    )

                    firestore.collection("announcements")
                        .document(id)
                        .set(data)
                        .await()

                    announcementDao.upsert(announcement)
                }

                _uiState.update {
                    it.copy(isLoading = false, isSuccess = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to save announcement"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
