package com.example.hmifu_mobile.feature.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.model.Event
import com.example.hmifu_mobile.model.UserSession
import com.example.hmifu_mobile.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Events screen
 */
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userSession: UserSession
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadEvents()
    }

    /**
     * Check if current user is admin
     */
    fun isAdmin(): Boolean = userSession.isAdmin()

    /**
     * Load events from repository
     */
    private fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            eventRepository.getEvents().collect { eventList ->
                _events.value = eventList
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresh events list
     */
    fun refresh() {
        loadEvents()
    }
}
