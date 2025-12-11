package com.example.hmifu_mobile.model

/**
 * UI state for the registration screen
 */
data class RegisterUiState(
    val nama: String = "",
    val nim: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isNamaValid: Boolean = true,
    val isNimValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isConfirmPasswordValid: Boolean = true,
    val namaError: String? = null,
    val nimError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val authState: AuthState = AuthState.Idle
) {
    /**
     * Check if form is valid for submission
     */
    val isFormValid: Boolean
        get() = nama.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                isNamaValid &&
                isNimValid &&
                isEmailValid &&
                isPasswordValid &&
                isConfirmPasswordValid &&
                password == confirmPassword

    /**
     * Check if currently loading
     */
    val isLoading: Boolean
        get() = authState is AuthState.Loading
}
