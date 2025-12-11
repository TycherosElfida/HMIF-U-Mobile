package com.example.hmifu_mobile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result wrapper for authentication operations.
 */
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    data object Loading : AuthResult<Nothing>()
}

/**
 * Repository for Firebase Authentication operations.
 */
@Suppress("unused")  // Public API properties kept for future use
@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    /**
     * Get the current authenticated user.
     */
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /**
     * Check if user is logged in.
     */
    val isLoggedIn: Boolean
        get() = currentUser != null

    /**
     * Observe authentication state changes.
     */
    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    /**
     * Login with email and password.
     */
    suspend fun login(email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                AuthResult.Success(it)
            } ?: AuthResult.Error("Login failed: User is null")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    /**
     * Register with email and password.
     */
    suspend fun register(email: String, password: String): AuthResult<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                AuthResult.Success(it)
            } ?: AuthResult.Error("Registration failed: User is null")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed")
        }
    }

    /**
     * Sign out the current user.
     */
    fun logout() {
        firebaseAuth.signOut()
    }

    /**
     * Send password reset email.
     */
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to send reset email")
        }
    }
}
