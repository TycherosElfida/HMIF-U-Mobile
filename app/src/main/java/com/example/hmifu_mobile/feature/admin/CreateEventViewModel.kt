package com.example.hmifu_mobile.feature.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.dao.EventDao
import com.example.hmifu_mobile.data.local.entity.EventCategory
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.repository.EventRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

/**
 * Create Event UI state.
 */
data class CreateEventUiState(
    val title: String = "",
    val description: String = "",
    val category: EventCategory = EventCategory.SEMINAR,
    val location: String = "",
    val isOnline: Boolean = false,
    val maxParticipants: String = "",
    val imageUrl: String = "",
    val id: String? = null,
    val startDate: Long = System.currentTimeMillis() + 86400000,
    val endDate: Long = System.currentTimeMillis() + 90000000,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean
        get() = title.isNotBlank() && description.isNotBlank() &&
                (isOnline || location.isNotBlank()) && startDate < endDate
}

/**
 * ViewModel for creating and editing events.
 */
@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val eventDao: EventDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    init {
        val eventId = savedStateHandle.get<String>("eventId")
        if (eventId != null) {
            loadEvent(eventId)
        }
    }

    private fun loadEvent(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val event = eventRepository.getById(id)
            if (event != null) {
                _uiState.update {
                    it.copy(
                        id = event.id,
                        title = event.title,
                        description = event.description,
                        category = EventCategory.fromString(event.category),
                        location = event.location,
                        isOnline = event.isOnline,
                        imageUrl = event.imageUrl ?: "",
                        maxParticipants = event.maxParticipants?.toString() ?: "",
                        startDate = event.startTime,
                        endDate = event.endTime,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Event not found") }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateCategory(category: EventCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun updateLocation(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun updateIsOnline(isOnline: Boolean) {
        _uiState.update { it.copy(isOnline = isOnline) }
    }

    fun updateMaxParticipants(max: String) {
        if (max.isEmpty() || max.all { it.isDigit() }) {
            _uiState.update { it.copy(maxParticipants = max) }
        }
    }

    fun updateImageUrl(url: String) {
        _uiState.update { it.copy(imageUrl = url) }
    }

    fun updateStartDate(date: Long) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun updateEndDate(date: Long) {
        _uiState.update { it.copy(endDate = date) }
    }

    fun saveEvent() {
        if (!uiState.value.isValid) {
            _uiState.update { it.copy(errorMessage = "Please fill in all required fields") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val state = _uiState.value
                val id = state.id ?: UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val organizerId = auth.currentUser?.uid ?: ""
                val maxPart = state.maxParticipants.toIntOrNull()
                val finalImageUrl = state.imageUrl.ifBlank { null }

                if (state.id != null) {
                    // Update existing
                    val originalEvent = eventRepository.getById(state.id)
                    val event = EventEntity(
                        id = id,
                        title = state.title,
                        description = state.description,
                        category = state.category.name,
                        location = if (state.isOnline) "Online" else state.location,
                        isOnline = state.isOnline,
                        meetingUrl = if (state.isOnline) state.location else originalEvent?.meetingUrl,
                        startTime = state.startDate,
                        endTime = state.endDate,
                        maxParticipants = maxPart,
                        currentParticipants = originalEvent?.currentParticipants ?: 0,
                        organizerId = originalEvent?.organizerId ?: organizerId,
                        imageUrl = finalImageUrl,
                        createdAt = originalEvent?.createdAt ?: now,
                        updatedAt = now
                    )
                    eventRepository.updateEvent(event).onFailure { throw it }
                } else {
                    // Create new
                    val event = EventEntity(
                        id = id,
                        title = state.title,
                        description = state.description,
                        category = state.category.name,
                        location = if (state.isOnline) "Online" else state.location,
                        isOnline = state.isOnline,
                        meetingUrl = if (state.isOnline) state.location else null,
                        startTime = state.startDate,
                        endTime = state.endDate,
                        maxParticipants = maxPart,
                        currentParticipants = 0,
                        organizerId = organizerId,
                        imageUrl = finalImageUrl,
                        createdAt = now,
                        updatedAt = now
                    )

                    val data = mapOf(
                        "id" to id,
                        "title" to state.title,
                        "description" to state.description,
                        "category" to state.category.name,
                        "location" to event.location,
                        "isOnline" to state.isOnline,
                        "meetingUrl" to event.meetingUrl,
                        "startTime" to state.startDate,
                        "endTime" to state.endDate,
                        "maxParticipants" to maxPart,
                        "currentParticipants" to 0,
                        "organizerId" to organizerId,
                        "imageUrl" to finalImageUrl,
                        "createdAt" to now,
                        "updatedAt" to now
                    )

                    firestore.collection("events")
                        .document(id)
                        .set(data)
                        .await()
                    
                    eventDao.upsert(event)
                }

                _uiState.update {
                    it.copy(isLoading = false, isSuccess = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to save event"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
