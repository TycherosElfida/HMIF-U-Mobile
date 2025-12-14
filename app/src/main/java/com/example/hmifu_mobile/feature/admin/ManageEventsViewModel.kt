package com.example.hmifu_mobile.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManageEventsUiState(
    val events: List<EventEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ManageEventsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    val uiState: StateFlow<ManageEventsUiState> = eventRepository
        .observeEvents()
        .map { events ->
            ManageEventsUiState(
                events = events,
                isLoading = false
            )
        }
        .catch { e ->
            emit(ManageEventsUiState(error = e.message))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ManageEventsUiState()
        )

    fun deleteEvent(id: String) {
        viewModelScope.launch {
            eventRepository.deleteEvent(id)
        }
    }
}
