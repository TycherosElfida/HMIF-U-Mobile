package com.example.hmifu_mobile.model

/**
 * Sealed class representing authentication states for the login flow.
 * Enables type-safe state management in ViewModel and UI.
 */
sealed class AuthState {
    /**
     * Initial idle state - no authentication action in progress
     */
    data object Idle : AuthState()
    
    /**
     * Loading state - authentication operation is in progress
     */
    data object Loading : AuthState()
    
    /**
     * Successfully authenticated state
     * @param user The authenticated user data
     */
    data class Authenticated(val user: User) : AuthState()
    
    /**
     * Authentication failed state
     * @param message Human-readable error message for display
     */
    data class Error(val message: String) : AuthState()
}

/**
 * UI state for the login screen
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val emailError: String? = null,
    val passwordError: String? = null,
    val authState: AuthState = AuthState.Idle
) {
    /**
     * Check if form is valid for submission
     */
    val isFormValid: Boolean
        get() = email.isNotBlank() && 
                password.isNotBlank() && 
                isEmailValid && 
                isPasswordValid
    
    /**
     * Check if currently loading
     */
    val isLoading: Boolean
        get() = authState is AuthState.Loading
}
