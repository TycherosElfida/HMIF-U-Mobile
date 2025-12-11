package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.CertificateDao
import com.example.hmifu_mobile.data.local.entity.CertificateEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Certificate operations.
 * Syncs certificates from Firestore `users/{uid}/certificates` to Room.
 */
@Singleton
class CertificateRepository @Inject constructor(
    private val certificateDao: CertificateDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    /**
     * Observe all certificates for the current user from Room.
     */
    fun observeCertificates(): Flow<List<CertificateEntity>> =
        certificateDao.observeAllCertificates()

    /**
     * Observe a single certificate.
     */
    fun observeCertificate(id: String): Flow<CertificateEntity?> =
        certificateDao.observeCertificate(id)

    /**
     * Sync certificates from Firestore to Room.
     * Listens to the `certificates` subcollection of the current user.
     */
    fun syncCertificates(): Flow<Result<Unit>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            trySend(Result.failure(IllegalStateException("User not logged in")))
            close()
            return@callbackFlow
        }

        val collection = firestore.collection("users")
            .document(currentUser.uid)
            .collection("certificates")

        val listener = collection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val certificates = querySnapshot.documents.mapNotNull { doc ->
                        try {
                            CertificateEntity(
                                id = doc.id,
                                eventId = doc.getString("eventId") ?: "",
                                eventTitle = doc.getString("eventTitle") ?: "Unknown Event",
                                recipientName = doc.getString("recipientName") ?: "",
                                recipientNim = doc.getString("recipientNim") ?: "",
                                fileUrl = doc.getString("fileUrl") ?: "",
                                issueDate = doc.getLong("issueDate") ?: System.currentTimeMillis(),
                                syncedAt = System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    ioScope.launch {
                        certificateDao.upsertCertificates(certificates)
                    }
                    trySend(Result.success(Unit))
                }
            }

        awaitClose { listener.remove() }
    }
}
