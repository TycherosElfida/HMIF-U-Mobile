package com.example.hmifu_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hmifu_mobile.data.local.entity.CertificateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CertificateDao {
    @Query("SELECT * FROM certificates ORDER BY issueDate DESC")
    fun observeAllCertificates(): Flow<List<CertificateEntity>>

    @Query("SELECT * FROM certificates WHERE id = :certificateId")
    fun observeCertificate(certificateId: String): Flow<CertificateEntity?>

    @Upsert
    suspend fun upsertCertificates(certificates: List<CertificateEntity>)

    @Query("DELETE FROM certificates")
    suspend fun clearAll()
}
