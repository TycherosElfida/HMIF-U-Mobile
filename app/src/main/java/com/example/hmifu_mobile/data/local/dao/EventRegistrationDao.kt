package com.example.hmifu_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.local.entity.EventRegistrationEntity
import com.example.hmifu_mobile.data.local.entity.RegistrationStatus
import kotlinx.coroutines.flow.Flow

/**
 * DAO for event registrations.
 */
@Dao
interface EventRegistrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(registration: EventRegistrationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(registrations: List<EventRegistrationEntity>)

    @Query("SELECT * FROM event_registrations WHERE userId = :userId ORDER BY registeredAt DESC")
    fun getRegistrationsByUser(userId: String): Flow<List<EventRegistrationEntity>>

    @Query("SELECT * FROM event_registrations WHERE eventId = :eventId")
    fun getRegistrationsByEvent(eventId: String): Flow<List<EventRegistrationEntity>>

    @Query("SELECT * FROM event_registrations WHERE eventId = :eventId AND userId = :userId LIMIT 1")
    suspend fun getRegistration(eventId: String, userId: String): EventRegistrationEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM event_registrations WHERE eventId = :eventId AND userId = :userId)")
    suspend fun isRegistered(eventId: String, userId: String): Boolean

    @Query("UPDATE event_registrations SET status = :status WHERE id = :registrationId")
    suspend fun updateStatus(registrationId: String, status: RegistrationStatus)

    @Query("UPDATE event_registrations SET checkedInAt = :timestamp, status = :status WHERE id = :registrationId")
    suspend fun checkIn(
        registrationId: String,
        timestamp: Long,
        status: RegistrationStatus = RegistrationStatus.CHECKED_IN
    )

    @Query("DELETE FROM event_registrations WHERE id = :registrationId")
    suspend fun delete(registrationId: String)

    @Query("DELETE FROM event_registrations WHERE eventId = :eventId AND userId = :userId")
    suspend fun deleteByEventAndUser(eventId: String, userId: String)

    @Query("DELETE FROM event_registrations")
    suspend fun deleteAll()

    /**
     * Get registered events for a user with full event details.
     */
    @Transaction
    @Query(
        """
        SELECT e.* FROM events e
        INNER JOIN event_registrations r ON e.id = r.eventId
        WHERE r.userId = :userId AND r.status = 'REGISTERED'
        ORDER BY e.startTime ASC
    """
    )
    fun getRegisteredEvents(userId: String): Flow<List<EventEntity>>

    /**
     * Get upcoming registered events for a user.
     */
    @Transaction
    @Query(
        """
        SELECT e.* FROM events e
        INNER JOIN event_registrations r ON e.id = r.eventId
        WHERE r.userId = :userId AND r.status = 'REGISTERED' AND e.startTime > :currentTime
        ORDER BY e.startTime ASC
    """
    )
    fun getUpcomingRegisteredEvents(userId: String, currentTime: Long): Flow<List<EventEntity>>

    /**
     * Get past registered events for a user.
     */
    @Transaction
    @Query(
        """
        SELECT e.* FROM events e
        INNER JOIN event_registrations r ON e.id = r.eventId
        WHERE r.userId = :userId AND e.endTime < :currentTime
        ORDER BY e.startTime DESC
    """
    )
    fun getPastRegisteredEvents(userId: String, currentTime: Long): Flow<List<EventEntity>>
}
