package com.example.hmifu_mobile.repository

import com.example.hmifu_mobile.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of AuthRepository.
 * Handles authentication via Firebase Auth and user data from Firestore.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Authenticate with Firebase Auth
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid 
                ?: return Result.failure(Exception("Login gagal: User tidak ditemukan"))
            
            // Fetch user data from Firestore
            val userDocument = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            
            if (!userDocument.exists()) {
                // User authenticated but no Firestore document - create basic user
                val basicUser = User(
                    uid = uid,
                    email = email,
                    nama = email.substringBefore("@"),
                    roles = listOf("member")
                )
                Result.success(basicUser)
            } else {
                // Map Firestore document to User model
                val user = User(
                    uid = uid,
                    email = userDocument.getString("email") ?: email,
                    nim = userDocument.getString("nim") ?: "",
                    nama = userDocument.getString("nama") ?: "",
                    angkatan = (userDocument.getLong("angkatan") ?: 0).toInt(),
                    roles = (userDocument.get("roles") as? List<*>)?.mapNotNull { it as? String } ?: listOf("member"),
                    techStacks = (userDocument.get("techStacks") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    profileImageUrl = userDocument.getString("profileImageUrl")
                )
                Result.success(user)
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("Email tidak terdaftar"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Password salah"))
        } catch (e: Exception) {
            Result.failure(Exception("Login gagal: ${e.localizedMessage ?: "Terjadi kesalahan"}"))
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        
        return try {
            val userDocument = firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .get()
                .await()
            
            if (!userDocument.exists()) {
                User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    nama = firebaseUser.displayName ?: "",
                    roles = listOf("member")
                )
            } else {
                User(
                    uid = firebaseUser.uid,
                    email = userDocument.getString("email") ?: firebaseUser.email ?: "",
                    nim = userDocument.getString("nim") ?: "",
                    nama = userDocument.getString("nama") ?: firebaseUser.displayName ?: "",
                    angkatan = (userDocument.getLong("angkatan") ?: 0).toInt(),
                    roles = (userDocument.get("roles") as? List<*>)?.mapNotNull { it as? String } ?: listOf("member"),
                    techStacks = (userDocument.get("techStacks") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    profileImageUrl = userDocument.getString("profileImageUrl")
                )
            }
        } catch (e: Exception) {
            // Return basic user info on Firestore failure
            User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                nama = firebaseUser.displayName ?: "",
                roles = listOf("member")
            )
        }
    }

    override fun observeAuthState(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                // For simplicity, emit basic user info synchronously
                trySend(
                    User(
                        uid = user.uid,
                        email = user.email ?: "",
                        nama = user.displayName ?: "",
                        roles = listOf("member")
                    )
                )
            } else {
                trySend(null)
            }
        }
        
        firebaseAuth.addAuthStateListener(authStateListener)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override fun isAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal mengirim email reset password: ${e.localizedMessage}"))
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        nama: String,
        nim: String
    ): Result<User> {
        return try {
            // Create user with Firebase Auth
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("Registrasi gagal: User tidak ditemukan"))

            // Create user document in Firestore
            val user = User(
                uid = uid,
                email = email,
                nim = nim,
                nama = nama,
                angkatan = 0, // Can be updated later
                roles = listOf("member"),
                techStacks = emptyList()
            )

            // Save to Firestore
            val userData = hashMapOf(
                "email" to user.email,
                "nim" to user.nim,
                "nama" to user.nama,
                "angkatan" to user.angkatan,
                "roles" to user.roles,
                "techStacks" to user.techStacks,
                "createdAt" to com.google.firebase.Timestamp.now()
            )

            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .set(userData)
                .await()

            Result.success(user)
        } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
            Result.failure(Exception("Email sudah terdaftar"))
        } catch (e: com.google.firebase.auth.FirebaseAuthWeakPasswordException) {
            Result.failure(Exception("Password terlalu lemah. Gunakan minimal 6 karakter"))
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Format email tidak valid"))
        } catch (e: Exception) {
            Result.failure(Exception("Registrasi gagal: ${e.localizedMessage ?: "Terjadi kesalahan"}"))
        }
    }
}
