package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.CandidateDao
import com.example.hmifu_mobile.data.local.dao.VoteRecordDao
import com.example.hmifu_mobile.data.local.entity.CandidateEntity
import com.example.hmifu_mobile.data.local.entity.VoteRecordEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ElectionRepository @Inject constructor(
    private val candidateDao: CandidateDao,
    private val voteRecordDao: VoteRecordDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val electionId = "default_election"
    private val candidatesCollection = firestore.collection("elections").document(electionId).collection("candidates")
    private val votesCollection = firestore.collection("elections").document(electionId).collection("votes")

    fun getCandidates(): Flow<List<CandidateEntity>> = candidateDao.getCandidates(electionId)

    fun getMyVote(): Flow<VoteRecordEntity?> = voteRecordDao.getVoteRecord(electionId)

    suspend fun syncCandidates() {
        try {
            val snapshot = candidatesCollection.get().await()
            val candidates = snapshot.documents.mapNotNull { doc ->
                if (doc.exists()) {
                    CandidateEntity(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        number = doc.getLong("number")?.toInt() ?: 0,
                        vision = doc.getString("vision") ?: "",
                        mission = doc.getString("mission") ?: "",
                        photoUrl = doc.getString("photoUrl"),
                        voteCount = doc.getLong("voteCount")?.toInt() ?: 0,
                        electionId = electionId
                    )
                } else null
            }
            // Simple sync
            candidates.forEach { candidateDao.upsert(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun vote(candidateId: String): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
        
        return try {
            // Check if already voted via Firestore transaction
            firestore.runTransaction { transaction ->
                val voteDocRef = votesCollection.document(uid)
                val voteDoc = transaction.get(voteDocRef)
                
                if (voteDoc.exists()) {
                    throw Exception("Already voted")
                }

                // Create vote record
                transaction.set(voteDocRef, mapOf(
                    "voterId" to uid,
                    "candidateId" to candidateId,
                    "timestamp" to System.currentTimeMillis()
                ))

                // Increment candidate count
                val candidateRef = candidatesCollection.document(candidateId)
                transaction.update(candidateRef, "voteCount", FieldValue.increment(1))
            }.await()

            // Save local record
            voteRecordDao.saveVote(VoteRecordEntity(electionId, candidateId))
            Result.success(Unit)
        } catch (e: Exception) {
            if (e.message?.contains("Already voted") == true) {
                // If failed because already voted, try to sync local state
                voteRecordDao.saveVote(VoteRecordEntity(electionId, "unknown")) // Just mark as voted
            }
            Result.failure(e)
        }
    }

    suspend fun addCandidate(candidate: CandidateEntity): Result<Unit> {
        return try {
             val data = mapOf(
                 "name" to candidate.name,
                 "number" to candidate.number,
                 "vision" to candidate.vision,
                 "mission" to candidate.mission,
                 "photoUrl" to candidate.photoUrl,
                 "voteCount" to 0
             )
            
            candidatesCollection.document(candidate.id).set(data).await()
            candidateDao.upsert(candidate)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Check remote if user has voted (initial check)
    suspend fun checkRemoteVoteStatus() {
        val uid = auth.currentUser?.uid ?: return
        try {
            val doc = votesCollection.document(uid).get().await()
            if (doc.exists()) {
                val candidateId = doc.getString("candidateId")
                voteRecordDao.saveVote(VoteRecordEntity(electionId, candidateId))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
