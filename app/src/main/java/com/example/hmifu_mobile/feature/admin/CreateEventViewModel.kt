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
    val startDate: Long = System.currentTimeMillis() + 86400000, // Default: tomorrow
    val endDate: Long = System.currentTimeMillis() + 90000000, // Default: tomorrow + 1 hour
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
    private val auth: FirebaseAuth
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

    fun createEvent() {
        if (!uiState.value.isValid) {
            _uiState.update { it.copy(errorMessage = "Please fill in all required fields") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val state = _uiState.value
                val id = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val organizerId = auth.currentUser?.uid ?: ""

                val maxPart = state.maxParticipants.toIntOrNull()

                val event = EventEntity(
                    id = id,
                    title = state.title,
                    description = state.description,
                    category = state.category.name,
                    location = if (state.isOnline) "Online" else state.location,
                    isOnline = state.isOnline,
                    startTime = state.startDate,
                    endTime = state.endDate,
                    maxParticipants = maxPart,
                    currentParticipants = 0,
                    organizerId = organizerId,
                    createdAt = now,
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
                    "startTime" to state.startDate,
                    "endTime" to state.endDate,
                    "maxParticipants" to maxPart,
                    "currentParticipants" to 0,
                    "organizerId" to organizerId,
                    "createdAt" to now,
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
                        errorMessage = e.message ?: "Failed to create event"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun reset() {
        _uiState.value = CreateEventUiState()
    }
}
