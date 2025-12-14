package com.example.hmifu_mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.hmifu_mobile.data.local.entity.ResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceDao {
    @Query("SELECT * FROM resources ORDER BY uploadedAt DESC")
    fun observeAllResources(): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE subject = :subject ORDER BY year DESC")
    fun observeBySubject(subject: String): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE semester = :semester ORDER BY subject, year DESC")
    fun observeBySemester(semester: Int): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE id = :resourceId")
    fun observeResource(resourceId: String): Flow<ResourceEntity?>

    @Query("SELECT * FROM resources WHERE id = :resourceId")
    suspend fun getResourceById(resourceId: String): ResourceEntity?

    @Query("SELECT DISTINCT subject FROM resources ORDER BY subject")
    fun observeSubjects(): Flow<List<String>>

    @Upsert
    suspend fun upsertResources(resources: List<ResourceEntity>)

    @Upsert
    suspend fun upsert(resource: ResourceEntity)

    @Query("DELETE FROM resources WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM resources")
    suspend fun clearAll()
}
