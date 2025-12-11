package com.example.hmifu_mobile.data.repository

import com.example.hmifu_mobile.data.local.dao.PollDao
import com.example.hmifu_mobile.data.local.entity.PollEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Poll operations.
 * Syncs polls from Firestore to Room and handles voting.
 */
@Singleton
class PollRepository @Inject constructor(
    private val pollDao: PollDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val collection = firestore.collection("polls")
    private val ioScope = CoroutineScope(Dispatchers.IO)

    /**
     * Observe active polls from Room.
     */
    fun observeActivePolls(): Flow<List<PollEntity>> = pollDao.observeActivePolls()

    /**
     * Observe all polls from Room.
     */
    fun observeAllPolls(): Flow<List<PollEntity>> = pollDao.observeAllPolls()

    /**
     * Observe a single poll.
     */
    fun observePoll(pollId: String): Flow<PollEntity?> = pollDao.observePoll(pollId)

    /**
     * Sync polls from Firestore to Room.
     */
    fun syncPolls(): Flow<Result<Unit>> = callbackFlow {
        val listener = collection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val currentUserId = auth.currentUser?.uid
                    val polls = querySnapshot.documents.mapNotNull { doc ->
                        try {
                            // Convert options map to JSON string
                            val optionsList = doc.get("options") as? List<*>
                            val optionsJson = JSONArray()
                            optionsList?.forEach { opt ->
                                val optMap = opt as? Map<*, *>
                                if (optMap != null) {
                                    val optJson = JSONObject()
                                    optJson.put("id", optMap["id"]?.toString() ?: "")
                                    optJson.put("text", optMap["text"]?.toString() ?: "")
                                    optJson.put("votes", (optMap["votes"] as? Number)?.toInt() ?: 0)
                                    optionsJson.put(optJson)
                                }
                            }

                            // Check if current user voted
                            val votes = doc.get("votes") as? Map<*, *>
                            val userVotedOptionId = if (currentUserId != null) {
                                votes?.get(currentUserId)?.toString()
                            } else null

                            PollEntity(
                                id = doc.id,
                                title = doc.getString("title") ?: "",
                                description = doc.getString("description") ?: "",
                                creatorId = doc.getString("creatorId") ?: "",
                                creatorName = doc.getString("creatorName") ?: "",
                                options = optionsJson.toString(),
                                isActive = doc.getBoolean("isActive") ?: true,
                                isMultipleChoice = doc.getBoolean("isMultipleChoice") ?: false,
                                expiresAt = doc.getLong("expiresAt"),
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                                totalVotes = doc.getLong("totalVotes")?.toInt() ?: 0,
                                userVotedOptionId = userVotedOptionId,
                                syncedAt = System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    ioScope.launch {
                        pollDao.upsertPolls(polls)
                    }
                    trySend(Result.success(Unit))
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Submit a vote for a poll option.
     */
    suspend fun vote(pollId: String, optionId: String): Result<Unit> {
        val currentUser = auth.currentUser ?: return Result.failure(
            IllegalStateException("User not logged in")
        )

        return try {
            firestore.runTransaction { transaction ->
                val pollRef = collection.document(pollId)
                val pollDoc = transaction.get(pollRef)

                // Update votes map
                val votes = pollDoc.get("votes") as? MutableMap<String, String> ?: mutableMapOf()
                val previousVote = votes[currentUser.uid]

                // Update options vote counts
                val options = pollDoc.get("options") as? MutableList<MutableMap<String, Any>>
                    ?: mutableListOf()

                // Decrement previous vote if exists
                if (previousVote != null) {
                    options.find { it["id"] == previousVote }?.let { opt ->
                        val currentVotes = (opt["votes"] as? Number)?.toInt() ?: 0
                        opt["votes"] = maxOf(0, currentVotes - 1)
                    }
                }

                // Increment new vote
                options.find { it["id"] == optionId }?.let { opt ->
                    val currentVotes = (opt["votes"] as? Number)?.toInt() ?: 0
                    opt["votes"] = currentVotes + 1
                }

                // Update user's vote
                votes[currentUser.uid] = optionId

                // Calculate total votes
                val totalVotes = options.sumOf { (it["votes"] as? Number)?.toInt() ?: 0 }

                transaction.update(
                    pollRef, mapOf(
                        "options" to options,
                        "votes" to votes,
                        "totalVotes" to totalVotes
                    )
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create a new poll (Admin only).
     */
    suspend fun createPoll(
        title: String,
        description: String,
        options: List<String>,
        isMultipleChoice: Boolean = false,
        expiresAt: Long? = null
    ): Result<String> {
        val currentUser = auth.currentUser ?: return Result.failure(
            IllegalStateException("User not logged in")
        )

        return try {
            val pollData = hashMapOf(
                "title" to title,
                "description" to description,
                "options" to options.mapIndexed { index, text ->
                    mapOf("id" to index.toString(), "text" to text, "votes" to 0)
                },
                "creatorId" to currentUser.uid,
                "creatorName" to (currentUser.displayName ?: "Admin"),
                "isActive" to true,
                "isMultipleChoice" to isMultipleChoice,
                "expiresAt" to expiresAt,
                "createdAt" to System.currentTimeMillis(),
                "totalVotes" to 0,
                "votes" to emptyMap<String, String>()
            )

            val docRef = collection.add(pollData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
