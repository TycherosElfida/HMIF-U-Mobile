package com.example.hmifu_mobile.feature.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.EventCategory
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Events screen.
 */
data class EventsUiState(
    val events: List<EventEntity> = emptyList(),
    val selectedCategory: EventCategory? = null,
    val showUpcomingOnly: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

/**
 * ViewModel for Events screen.
 */
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
        startFirestoreSync()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            eventRepository.observeUpcoming().collect { events ->
                _uiState.update {
                    it.copy(
                        events = events,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun startFirestoreSync() {
        viewModelScope.launch {
            eventRepository.syncFromFirestore().collect { result ->
                result.onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }

    fun toggleUpcomingOnly(showUpcoming: Boolean) {
        _uiState.update { it.copy(showUpcomingOnly = showUpcoming) }

        viewModelScope.launch {
            val flow = if (showUpcoming) {
                eventRepository.observeUpcoming()
            } else {
                eventRepository.observeEvents()
            }

            flow.collect { events ->
                _uiState.update { it.copy(events = events) }
            }
        }
    }

    fun selectCategory(category: EventCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }

        viewModelScope.launch {
            if (category == null) {
                eventRepository.observeEvents().collect { events ->
                    _uiState.update { it.copy(events = events) }
                }
            } else {
                eventRepository.observeByCategory(category.name).collect { events ->
                    _uiState.update { it.copy(events = events) }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            eventRepository.deleteEvent(eventId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    // Refresh is handled by Flow observation
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = eventRepository.refresh()
            result.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}
