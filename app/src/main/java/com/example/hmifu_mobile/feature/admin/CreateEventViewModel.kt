package com.example.hmifu_mobile.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.dao.EventDao
import com.example.hmifu_mobile.data.local.entity.EventCategory
import com.example.hmifu_mobile.data.local.entity.EventEntity
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
    val startDate: Long = System.currentTimeMillis() + 86400000,
    val endDate: Long = System.currentTimeMillis() + 90000000,
    val selectedImageUri: android.net.Uri? = null,
    val existingImageUrl: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean
        get() = title.isNotBlank() && description.isNotBlank() &&
                (isOnline || location.isNotBlank()) && startDate < endDate
}

/**
 * ViewModel for creating events.
 */
@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventDao: EventDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val imageRepository: com.example.hmifu_mobile.data.repository.ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

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
        // Only allow numeric input
        if (max.isEmpty() || max.all { it.isDigit() }) {
            _uiState.update { it.copy(maxParticipants = max) }
        }
    }

    fun updateStartDate(date: Long) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun updateEndDate(date: Long) {
        _uiState.update { it.copy(endDate = date) }
    }

    fun updateSelectedImage(uri: android.net.Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    private var editEventId: String? = null

    fun loadEvent(eventId: String) {
        if (editEventId == eventId) return 
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val event = eventDao.getById(eventId)
            if (event != null) {
                editEventId = event.id
                _uiState.update {
                    it.copy(
                        title = event.title,
                        description = event.description,
                        category = try { EventCategory.valueOf(event.category) } catch(e: Exception) { EventCategory.SEMINAR },
                        location = if (event.isOnline) "Online" else event.location,
                        isOnline = event.isOnline,
                        maxParticipants = event.maxParticipants?.toString() ?: "",
                        startDate = event.startTime,
                        endDate = event.endTime,
                        existingImageUrl = event.imageUrl,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Event not found") }
            }
        }
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
                // Use existing ID if editing, else generate new
                val id = editEventId ?: UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val organizerId = auth.currentUser?.uid ?: ""

                // Upload image if selected
                var finalImageUrl: String? = null
                
                // If new image selected, upload it
                if (state.selectedImageUri != null) {
                    val uploadResult = imageRepository.uploadImage(state.selectedImageUri!!, "events")
                    if (uploadResult.isSuccess) {
                        finalImageUrl = uploadResult.getOrNull()
                    } else {
                         _uiState.update { 
                             it.copy(isLoading = false, errorMessage = "Failed to upload image: ${uploadResult.exceptionOrNull()?.message}") 
                         }
                        return@launch
                    }
                } else if (editEventId != null) {
                    // If editing and no new image, keep existing?
                    // We need to fetch existing event to know old URL if we don't store it in state
                    val oldEvent = eventDao.getById(editEventId!!)
                    finalImageUrl = oldEvent?.imageUrl
                }

                val maxPart = state.maxParticipants.toIntOrNull()

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
                    currentParticipants = if (editEventId != null) eventDao.getById(id)?.currentParticipants ?: 0 else 0,
                    organizerId = organizerId,
                    imageUrl = finalImageUrl,
                    createdAt = if (editEventId != null) eventDao.getById(id)?.createdAt ?: now else now,
                    updatedAt = now
                )

                // Save to Firestore
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
                    "currentParticipants" to event.currentParticipants,
                    "organizerId" to organizerId,
                    "imageUrl" to finalImageUrl,
                    "createdAt" to event.createdAt,
                    "updatedAt" to now
                )

                firestore.collection("events")
                    .document(id)
                    .set(data)
                    .await()

                // Save to local DB
                eventDao.upsert(event)

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

    fun deleteEvent() {
        if (editEventId == null) return
        
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
             try {
                 firestore.collection("events").document(editEventId!!).delete().await()
                 eventDao.delete(editEventId!!)
                 _uiState.update { it.copy(isLoading = false, isSuccess = true) }
             } catch (e: Exception) {
                 _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
             }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun reset() {
        editEventId = null
        _uiState.value = CreateEventUiState()
    }
}
