package com.example.hmifu_mobile.feature.profile

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
 * Edit Profile UI state.
 */
data class EditProfileUiState(
    val name: String = "",
    val nim: String = "",
    val angkatan: String = "",
    val concentration: String = "",
    val techStack: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && nim.isNotBlank() && angkatan.isNotBlank()
}

/**
 * ViewModel for editing user profile.
 */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            try {
                val result = userRepository.syncCurrentUser()
                result.onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            name = profile.name,
                            nim = profile.nim,
                            angkatan = profile.angkatan,
                            concentration = profile.concentration,
                            techStack = profile.techStack
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load profile"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

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

    fun saveProfile() {
        if (!uiState.value.isValid) {
            _uiState.update { it.copy(errorMessage = "Please fill in required fields") }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val state = _uiState.value
                val updatedProfile = UserProfile(
                    uid = "", // Will be set by repository
                    email = "", // Will be preserved by repository
                    name = state.name,
                    nim = state.nim,
                    angkatan = state.angkatan,
                    concentration = state.concentration,
                    techStack = state.techStack
                )

                val result = userRepository.updateProfile(updatedProfile)
                result.onSuccess {
                    _uiState.update {
                        it.copy(isSaving = false, isSuccess = true)
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = e.message ?: "Failed to save profile"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
