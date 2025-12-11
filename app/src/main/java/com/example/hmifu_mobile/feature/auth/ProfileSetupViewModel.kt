package com.example.hmifu_mobile.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.repository.UserProfile
import com.example.hmifu_mobile.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Profile setup screen.
 */
data class ProfileSetupUiState(
    val name: String = "",
    val nim: String = "",
    val angkatan: String = "",
    val concentration: String = "",
    val techStack: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isComplete: Boolean = false
)

/**
 * ViewModel for Profile setup screen.
 */
@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateNim(nim: String) {
        _uiState.update { it.copy(nim = nim) }
    }

    fun updateAngkatan(angkatan: String) {
        _uiState.update { it.copy(angkatan = angkatan) }
    }

    fun updateConcentration(concentration: String) {
        _uiState.update { it.copy(concentration = concentration) }
    }

    fun updateTechStack(techStack: String) {
        _uiState.update { it.copy(techStack = techStack) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun saveProfile() {
        val state = _uiState.value

        // Validate required fields
        when {
            state.name.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Name is required") }
                return
            }

            state.nim.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "NIM is required") }
                return
            }

            state.angkatan.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Angkatan is required") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val uid = userRepository.currentUserId
            val email = userRepository.currentUserEmail

            if (uid == null) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Not logged in")
                }
                return@launch
            }

            val profile = UserProfile(
                uid = uid,
                email = email ?: "",
                name = state.name,
                nim = state.nim,
                angkatan = state.angkatan,
                concentration = state.concentration,
                techStack = state.techStack
            )

            val result = userRepository.updateProfile(profile)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isComplete = true) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to save profile"
                    )
                }
            }
        }
    }
}
