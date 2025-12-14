package com.example.hmifu_mobile.feature.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.repository.UserProfile
import com.example.hmifu_mobile.data.repository.UserRepository
import com.example.hmifu_mobile.util.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val photoBlob: ByteArray? = null,
    val role: String = "member", // Preserve role
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() && nim.isNotBlank() && angkatan.isNotBlank()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EditProfileUiState

        if (name != other.name) return false
        if (nim != other.nim) return false
        if (angkatan != other.angkatan) return false
        if (concentration != other.concentration) return false
        if (techStack != other.techStack) return false
        if (photoBlob != null) {
            if (other.photoBlob == null) return false
            if (!photoBlob.contentEquals(other.photoBlob)) return false
        } else if (other.photoBlob != null) return false
        if (role != other.role) return false
        if (isLoading != other.isLoading) return false
        if (isSaving != other.isSaving) return false
        if (isSuccess != other.isSuccess) return false
        if (errorMessage != other.errorMessage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + nim.hashCode()
        result = 31 * result + angkatan.hashCode()
        result = 31 * result + concentration.hashCode()
        result = 31 * result + techStack.hashCode()
        result = 31 * result + (photoBlob?.contentHashCode() ?: 0)
        result = 31 * result + role.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + isSaving.hashCode()
        result = 31 * result + isSuccess.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        return result
    }
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
                            techStack = profile.techStack,
                            photoBlob = profile.photoBlob,
                            role = profile.role // Load existing role
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

    fun updatePhoto(context: Context, uri: Uri) {
        viewModelScope.launch {
            val bytes = withContext(Dispatchers.IO) {
                ImageUtils.uriToBytes(context, uri)
            }
            if (bytes != null) {
                _uiState.update { it.copy(photoBlob = bytes) }
            } else {
                _uiState.update { it.copy(errorMessage = "Failed to load image") }
            }
        }
    }

    fun saveProfile() {
        if (!uiState.value.isValid) {
            _uiState.update { it.copy(errorMessage = "Please fill in required fields") }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val currentUid = userRepository.currentUserId ?: return@launch
                val state = _uiState.value
                val updatedProfile = UserProfile(
                    uid = currentUid, // Set the correct UID
                    email = userRepository.currentUserEmail ?: "", // Set current email
                    name = state.name,
                    nim = state.nim,
                    angkatan = state.angkatan,
                    concentration = state.concentration,
                    techStack = state.techStack,
                    photoBlob = state.photoBlob,
                    role = state.role // Preserve role
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
