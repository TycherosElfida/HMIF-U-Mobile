package com.example.hmifu_mobile.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.AnnouncementCategory
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.repository.AnnouncementRepository
import com.example.hmifu_mobile.data.repository.UserRepository
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
data class HomeUiState(
    val announcements: List<AnnouncementEntity> = emptyList(),
    val selectedCategory: AnnouncementCategory? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // User profile data
    val userName: String = "",
    val userNim: String = "",
    val userAngkatan: String = "",
    val userPoints: Int = 0,
    val hasUnreadNotifications: Boolean = false
)

/**
 * ViewModel for Home screen with announcement feed.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        loadAnnouncements()
        startFirestoreSync()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            userRepository.syncCurrentUser().onSuccess { profile ->
                _uiState.update {
                    it.copy(
                        userName = profile.name.ifBlank { "Member" },
                        userNim = profile.nim,
                        userAngkatan = profile.angkatan,
                        userPoints = 0 // TODO: Implement points system
                    )
                }
            }

            // Also observe local user changes
            userRepository.observeCurrentUser().collect { userEntity ->
                userEntity?.let { user ->
                    _uiState.update {
                        it.copy(
                            userName = user.name.ifBlank { "Member" },
                            userNim = user.nim,
                            userAngkatan = user.angkatan,
                            userPoints = user.points
                        )
                    }
                }
            }
        }
    }

    private fun loadAnnouncements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            announcementRepository.observeAnnouncements().collect { announcements ->
                _uiState.update {
                    it.copy(
                        announcements = announcements,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun startFirestoreSync() {
        viewModelScope.launch {
            announcementRepository.syncFromFirestore().collect { result ->
                result.onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
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
