package com.example.hmifu_mobile.feature.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.dao.EventDao
import com.example.hmifu_mobile.data.local.dao.EventRegistrationDao
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.local.entity.EventRegistrationEntity
import com.example.hmifu_mobile.data.repository.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Registrant with user profile.
 */
data class RegistrantInfo(
    val registration: EventRegistrationEntity,
    val profile: UserProfile? = null
)

/**
 * Event registrants UI state.
 */
data class EventRegistrantsUiState(
    val isLoading: Boolean = true,
    val event: EventEntity? = null,
    val registrants: List<RegistrantInfo> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ViewModel for viewing event registrants.
 */
@HiltViewModel
class EventRegistrantsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val eventDao: EventDao,
    private val eventRegistrationDao: EventRegistrationDao,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val eventId: String = savedStateHandle["eventId"] ?: ""

    private val _uiState = MutableStateFlow(EventRegistrantsUiState())
    val uiState: StateFlow<EventRegistrantsUiState> = _uiState.asStateFlow()

    init {
        loadEventAndRegistrants()
    }

    private fun loadEventAndRegistrants() {
        viewModelScope.launch {
            try {
                // Load event details
                val event = eventDao.getById(eventId)
                _uiState.update { it.copy(event = event) }

                // Load registrations
                eventRegistrationDao.getRegistrationsByEvent(eventId)
                    .collect { registrations ->
                        // Fetch user profiles for each registration
                        val registrantInfoList = registrations.map { registration ->
                            val profile = fetchUserProfile(registration.userId)
                            RegistrantInfo(registration, profile)
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                registrants = registrantInfoList
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load registrants"
                    )
                }
            }
        }
    }

    private suspend fun fetchUserProfile(userId: String): UserProfile? {
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            if (doc.exists()) {
                UserProfile(
                    uid = userId,
                    email = doc.getString("email") ?: "",
                    name = doc.getString("name") ?: "",
                    nim = doc.getString("nim") ?: "",
                    angkatan = doc.getString("angkatan") ?: ""
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadEventAndRegistrants()
    }
}
