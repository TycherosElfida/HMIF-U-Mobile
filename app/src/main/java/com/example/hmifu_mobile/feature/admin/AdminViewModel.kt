package com.example.hmifu_mobile.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.dao.AnnouncementDao
import com.example.hmifu_mobile.data.local.dao.EventDao
import com.example.hmifu_mobile.data.local.dao.EventRegistrationDao
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Admin dashboard UI state.
 */
data class AdminUiState(
    val isLoading: Boolean = true,
    val isAdmin: Boolean = false,
    val totalAnnouncements: Int = 0,
    val totalEvents: Int = 0,
    val recentAnnouncements: List<AnnouncementEntity> = emptyList(),
    val recentEvents: List<EventEntity> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ViewModel for Admin dashboard.
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val announcementDao: AnnouncementDao,
    private val eventDao: EventDao,
    @Suppress("unused") private val eventRegistrationDao: EventRegistrationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        checkAdminRole()
        loadDashboardData()
    }

    private fun checkAdminRole() {
        viewModelScope.launch {
            val result = userRepository.syncCurrentUser()
            result.onSuccess { profile ->
                val isAdmin = profile.role in listOf("admin", "moderator", "staff")
                _uiState.update { it.copy(isAdmin = isAdmin) }
            }.onFailure {
                _uiState.update { it.copy(isAdmin = false) }
            }
        }
    }

    private fun loadDashboardData() {
        // Load recent announcements
        viewModelScope.launch {
            announcementDao.observeAll().collect { announcements ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalAnnouncements = announcements.size,
                        recentAnnouncements = announcements.take(5)
                    )
                }
            }
        }

        // Load recent events
        viewModelScope.launch {
            eventDao.observeAll().collect { events ->
                _uiState.update {
                    it.copy(
                        totalEvents = events.size,
                        recentEvents = events.take(5)
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }


}
