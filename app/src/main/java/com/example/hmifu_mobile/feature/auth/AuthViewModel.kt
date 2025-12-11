package com.example.hmifu_mobile.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.repository.AuthRepository
import com.example.hmifu_mobile.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for authentication screens.
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val needsProfileSetup: Boolean = false
)

/**
 * ViewModel for authentication flows.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Check if user is already logged in
        if (authRepository.isLoggedIn) {
            _uiState.update { it.copy(isAuthenticated = true) }
        }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Login with email and password.
     */
    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email is required") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Password is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.login(email, password)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            errorMessage = null
                        )
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }

                is AuthResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    /**
     * Register with email and password.
     */
    fun register() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email is required") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Password is required") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.register(email, password)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            needsProfileSetup = true,
                            errorMessage = null
                        )
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }

                is AuthResult.Loading -> {
                    // Already handled
                }
            }
        }
    }

    /**
     * Logout the current user.
     */
    fun logout() {
        authRepository.logout()
        _uiState.update {
            AuthUiState() // Reset to initial state
        }
    }

    /**
     * Send password reset email.
     */
    fun sendPasswordReset() {
        val email = _uiState.value.email.trim()

        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Password reset email sent!"
                        )
                    }
                }

                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }

                is AuthResult.Loading -> {
                    // Already handled
                }
            }
        }
    }
}
