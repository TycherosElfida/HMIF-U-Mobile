package com.example.hmifu_mobile.feature.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.dao.EventRegistrationDao
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyEventsUiState(
    val upcomingEvents: List<EventEntity> = emptyList(),
    val pastEvents: List<EventEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MyEventsViewModel @Inject constructor(
    private val eventRegistrationDao: EventRegistrationDao,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyEventsUiState())
    val uiState: StateFlow<MyEventsUiState> = _uiState.asStateFlow()

    init {
        loadMyEvents()
    }

    private fun loadMyEvents() {
        val user = authRepository.currentUser
        if (user != null) {
            viewModelScope.launch {
                val currentTime = System.currentTimeMillis()
                
                // We could combine flows, but executing two separate collections is simpler for now
                // Or better, launch two coroutines to updates state concurrently
                
                launch {
                    eventRegistrationDao.getUpcomingRegisteredEvents(user.uid, currentTime).collectLatest { events ->
                        _uiState.value = _uiState.value.copy(upcomingEvents = events, isLoading = false)
                    }
                }
                
                launch {
                    eventRegistrationDao.getPastRegisteredEvents(user.uid, currentTime).collectLatest { events ->
                        _uiState.value = _uiState.value.copy(pastEvents = events) // Maintain loading if desired, but upcoming is prio
                    }
                }
            }
        } else {
             _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
