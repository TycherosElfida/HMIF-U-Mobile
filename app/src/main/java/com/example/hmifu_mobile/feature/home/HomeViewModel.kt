package com.example.hmifu_mobile.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.model.Event
import com.example.hmifu_mobile.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for HomeScreen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            eventRepository.getEvents().collect { eventList ->
                // Show only first 5 events on home
                _events.value = eventList.take(5)
                _isLoading.value = false
            }
        }
    }
}
