package com.example.hmifu_mobile.feature.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.model.AuthState
import com.example.hmifu_mobile.model.LoginUiState
import com.example.hmifu_mobile.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Login screen.
 * Manages form state, validation, and authentication logic.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Update email field and validate
     */
    fun onEmailChange(email: String) {
        _uiState.update { state ->
            val isValid = email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
            state.copy(
                email = email,
                isEmailValid = isValid,
                emailError = if (!isValid) "Format email tidak valid" else null
            )
        }
    }

    /**
     * Update password field and validate
     */
    fun onPasswordChange(password: String) {
        _uiState.update { state ->
            val isValid = password.isBlank() || password.length >= 6
            state.copy(
                password = password,
                isPasswordValid = isValid,
                passwordError = if (!isValid) "Password minimal 6 karakter" else null
            )
        }
    }

    /**
     * Perform login action
     */
    fun login() {
        val currentState = _uiState.value
        
        // Validate before attempting login
        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email tidak boleh kosong") }
            return
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.update { it.copy(emailError = "Format email tidak valid", isEmailValid = false) }
            return
        }
        
        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Password tidak boleh kosong") }
            return
        }
        
        if (currentState.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password minimal 6 karakter", isPasswordValid = false) }
            return
        }

        // Set loading state
        _uiState.update { it.copy(authState = AuthState.Loading) }

        viewModelScope.launch {
            val result = authRepository.login(currentState.email, currentState.password)
            
            result.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(authState = AuthState.Authenticated(user)) }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(authState = AuthState.Error(exception.message ?: "Login gagal"))
                    }
                }
            )
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.update { it.copy(authState = AuthState.Idle) }
    }

    /**
     * Reset UI state
     */
    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
