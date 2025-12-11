package com.example.hmifu_mobile.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.AnnouncementCategory
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Home screen.
 */
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.repository.EventRepository

/**
 * UI State for Home screen.
 */
data class HomeUiState(
    val announcements: List<AnnouncementEntity> = emptyList(),
    val featuredEvent: EventEntity? = null,
    val selectedCategory: AnnouncementCategory? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for Home screen with announcement feed.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        startFirestoreSync()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Combine announcements and featured event loading
            launch {
                announcementRepository.observeAnnouncements().collect { announcements ->
                    _uiState.update {
                        it.copy(
                            announcements = announcements,
                            isLoading = false
                        )
                    }
                }
            }

            launch {
                eventRepository.observeUpcoming().collect { events ->
                    _uiState.update {
                        it.copy(
                            featuredEvent = events.firstOrNull()
                        )
                    }
                }
            }
        }
    }

    private fun startFirestoreSync() {
        viewModelScope.launch {
            launch {
                announcementRepository.syncFromFirestore().collect { result ->
                    result.onFailure { error ->
                        _uiState.update { it.copy(errorMessage = error.message) }
                    }
                }
            }
            launch {
                eventRepository.syncFromFirestore().collect { result ->
                     result.onFailure { error ->
                        // Log error but don't disrupt UI too much for background sync
                        // _uiState.update { it.copy(errorMessage = error.message) } 
                     }
                }
            }
        }
    }

    fun selectCategory(category: AnnouncementCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }

        viewModelScope.launch {
            if (category == null) {
                announcementRepository.observeAnnouncements().collect { announcements ->
                    _uiState.update { it.copy(announcements = announcements) }
                }
            } else {
                announcementRepository.observeByCategory(category.name).collect { announcements ->
                    _uiState.update { it.copy(announcements = announcements) }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
