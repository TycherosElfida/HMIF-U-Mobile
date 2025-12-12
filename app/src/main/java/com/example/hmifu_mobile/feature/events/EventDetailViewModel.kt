package com.example.hmifu_mobile.feature.events

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.repository.EventRepository
import com.example.hmifu_mobile.data.repository.UserRepository
import com.example.hmifu_mobile.data.repository.EventRegistrationRepository
import com.example.hmifu_mobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val eventRegistrationRepository: EventRegistrationRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = checkNotNull(savedStateHandle["eventId"])

    private val _event = MutableStateFlow<EventEntity?>(null)
    val event: StateFlow<EventEntity?> = _event.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val isRegistered: StateFlow<Boolean> = _event
        .flatMapLatest { event ->
            val user = authRepository.currentUser
            if (event == null || user == null) {
                flowOf(false)
            } else {
                eventRegistrationRepository.isRegistered(event.id, user.uid)
                    .map { param -> param.any { it.eventId == event.id } }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isEditable: StateFlow<Boolean> = combine(
        _event,
        userRepository.observeCurrentUser()
    ) { event, user ->
        if (event == null || user == null) false
        else {
            val role = user.role.lowercase()
            val isAdmin = role in listOf("admin", "president", "secretary", "vice_president")
            val isOwner = event.organizerId == user.uid
            isAdmin || isOwner
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        loadEvent()
    }

    private fun loadEvent() {
        viewModelScope.launch {
            eventRepository.observeById(eventId).collectLatest {
                _event.value = it
            }
        }
    }

    fun toggleRegistration() {
        viewModelScope.launch {
            val event = _event.value ?: return@launch
            val user = authRepository.currentUser ?: return@launch
            
            if (isRegistered.value) {
                eventRegistrationRepository.cancelRegistration(event, user.uid)
            } else {
                eventRegistrationRepository.registerEvent(event, user)
            }
        }
    }
}
