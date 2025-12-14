package com.example.hmifu_mobile.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManageAnnouncementsUiState(
    val announcements: List<AnnouncementEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ManageAnnouncementsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    val uiState: StateFlow<ManageAnnouncementsUiState> = announcementRepository
        .observeAnnouncements()
        .map { announcements ->
            ManageAnnouncementsUiState(
                announcements = announcements,
                isLoading = false
            )
        }
        .catch { e ->
            emit(ManageAnnouncementsUiState(error = e.message))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ManageAnnouncementsUiState()
        )

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch {
            announcementRepository.delete(id)
        }
    }
}
