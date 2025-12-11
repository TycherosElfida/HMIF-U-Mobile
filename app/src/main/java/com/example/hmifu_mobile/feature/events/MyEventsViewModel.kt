package com.example.hmifu_mobile.feature.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.dao.EventRegistrationDao
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * My Events screen UI state.
 */
data class MyEventsUiState(
    val isLoading: Boolean = true,
    val upcomingEvents: List<EventEntity> = emptyList(),
    val pastEvents: List<EventEntity> = emptyList(),
    val selectedTab: Int = 0, // 0 = Upcoming, 1 = Past
    val errorMessage: String? = null
)

/**
 * ViewModel for My Events screen.
 */
@HiltViewModel
class MyEventsViewModel @Inject constructor(
    private val eventRegistrationDao: EventRegistrationDao,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyEventsUiState())
    val uiState: StateFlow<MyEventsUiState> = _uiState.asStateFlow()

    init {
        loadMyEvents()
    }

    private fun loadMyEvents() {
        val userId = auth.currentUser?.uid ?: run {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Not logged in") }
            return
        }

        val currentTime = System.currentTimeMillis()

        // Load upcoming events
        viewModelScope.launch {
            eventRegistrationDao.getUpcomingRegisteredEvents(userId, currentTime)
                .collect { events ->
                    _uiState.update { 
                        it.copy(isLoading = false, upcomingEvents = events) 
                    }
                }
        }

        // Load past events
        viewModelScope.launch {
            eventRegistrationDao.getPastRegisteredEvents(userId, currentTime)
                .collect { events ->
                    _uiState.update { 
                        it.copy(pastEvents = events) 
                    }
                }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadMyEvents()
    }
}
