package com.example.hmifu_mobile.feature.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.model.AuthState
import com.example.hmifu_mobile.model.RegisterUiState
import com.example.hmifu_mobile.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Register screen.
 * Manages form state, validation, and registration logic.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Update nama field and validate
     */
    fun onNamaChange(nama: String) {
        _uiState.update { state ->
            val isValid = nama.isBlank() || nama.length >= 3
            state.copy(
                nama = nama,
                isNamaValid = isValid,
                namaError = if (!isValid) "Nama minimal 3 karakter" else null
            )
        }
    }

    /**
     * Update NIM field and validate
     */
    fun onNimChange(nim: String) {
        _uiState.update { state ->
            val isValid = nim.isBlank() || nim.matches(Regex("^[0-9]{8,12}$"))
            state.copy(
                nim = nim,
                isNimValid = isValid,
                nimError = if (!isValid) "NIM harus 8-12 digit angka" else null
            )
        }
    }

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
            val confirmValid = state.confirmPassword.isBlank() || state.confirmPassword == password
            state.copy(
                password = password,
                isPasswordValid = isValid,
                passwordError = if (!isValid) "Password minimal 6 karakter" else null,
                isConfirmPasswordValid = confirmValid,
                confirmPasswordError = if (!confirmValid) "Password tidak cocok" else null
            )
        }
    }

    /**
     * Update confirm password field and validate
     */
    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { state ->
            val isValid = confirmPassword.isBlank() || confirmPassword == state.password
            state.copy(
                confirmPassword = confirmPassword,
                isConfirmPasswordValid = isValid,
                confirmPasswordError = if (!isValid) "Password tidak cocok" else null
            )
        }
    }

    /**
     * Perform registration action
     */
    fun register() {
        val currentState = _uiState.value

        // Validate all fields
        if (currentState.nama.isBlank()) {
            _uiState.update { it.copy(namaError = "Nama tidak boleh kosong") }
            return
        }

        if (currentState.nim.isNotBlank() && !currentState.nim.matches(Regex("^[0-9]{8,12}$"))) {
            _uiState.update { it.copy(nimError = "NIM harus 8-12 digit angka", isNimValid = false) }
            return
        }

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

        if (currentState.confirmPassword != currentState.password) {
            _uiState.update { it.copy(confirmPasswordError = "Password tidak cocok", isConfirmPasswordValid = false) }
            return
        }

        // Set loading state
        _uiState.update { it.copy(authState = AuthState.Loading) }

        viewModelScope.launch {
            val result = authRepository.register(
                email = currentState.email,
                password = currentState.password,
                nama = currentState.nama,
                nim = currentState.nim
            )

            result.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(authState = AuthState.Authenticated(user)) }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(authState = AuthState.Error(exception.message ?: "Registrasi gagal"))
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
        _uiState.value = RegisterUiState()
    }
}
