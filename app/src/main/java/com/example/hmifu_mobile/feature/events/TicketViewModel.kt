package com.example.hmifu_mobile.feature.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.repository.EventRepository
import com.example.hmifu_mobile.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TicketUiState(
    val event: EventEntity? = null,
    val userId: String = "",
    val userName: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketUiState())
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            userRepository.syncCurrentUser()
            userRepository.observeCurrentUser().collect { user ->
                if (user != null) {
                    _uiState.update { 
                        it.copy(
                            userId = user.uid, // Assuming id matches uid basically or is the key
                            userName = user.name
                        ) 
                    }
                }
            }
        }
    }

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Assuming we can get event by ID from repo or existing list observing
            // Creating a simple getEventById in repo might be needed if not exist. 
            // For now let's try to get from observed list or fetch.
            
            // NOTE: EventRepository observeEvents() returns a Flow list. 
            // Better to add getEvent(id) in Repository or find it from list.
            // Let's implement a quick find in VM for now from repo flow if needed, OR add getEvent to Repo.
            // Let's check EventRepository again.
            
            // HACK: Just observing all events and filtering for now to save Repo edits, 
            // but ideally we add getEvent(id).
            eventRepository.observeEvents().collect { events ->
                val event = events.find { it.id == eventId }
                _uiState.update { 
                    it.copy(
                        event = event,
                        isLoading = false
                    ) 
                }
            }
        }
    }
}
