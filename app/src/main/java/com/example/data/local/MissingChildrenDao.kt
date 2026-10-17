package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CaseEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.SightingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MissingChildrenDao {

    // Cases
    @Query("SELECT * FROM missing_cases ORDER BY createdAt DESC")
    fun getAllCasesFlow(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM missing_cases WHERE status = 'Active' ORDER BY createdAt DESC")
    fun getActiveCasesFlow(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM missing_cases WHERE status = 'Resolved' ORDER BY updatedAt DESC")
    fun getResolvedCasesFlow(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM missing_cases WHERE status = 'Under Review' ORDER BY createdAt DESC")
    fun getUnderReviewCasesFlow(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM missing_cases WHERE ownerUid = :ownerUid ORDER BY createdAt DESC")
    fun getCasesByOwnerFlow(ownerUid: String): Flow<List<CaseEntity>>

    @Query("SELECT * FROM missing_cases WHERE caseId = :caseId LIMIT 1")
    fun getCaseByIdFlow(caseId: String): Flow<CaseEntity?>

    @Query("SELECT * FROM missing_cases WHERE caseId = :caseId LIMIT 1")
    suspend fun getCaseById(caseId: String): CaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(caseEntity: CaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCases(cases: List<CaseEntity>)

    @Update
    suspend fun updateCase(caseEntity: CaseEntity)

    @Query("UPDATE missing_cases SET status = :status, rejectionReason = :reason, updatedAt = :updatedAt WHERE caseId = :caseId")
    suspend fun updateCaseModeration(caseId: String, status: String, reason: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE missing_cases SET status = 'Resolved', resolutionNotes = :resolutionNotes, resolvedDate = :resolvedDate, updatedAt = :updatedAt WHERE caseId = :caseId")
    suspend fun resolveCase(caseId: String, resolutionNotes: String, resolvedDate: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE missing_cases SET sightingsCount = sightingsCount + 1, updatedAt = :updatedAt WHERE caseId = :caseId")
    suspend fun incrementSightingsCount(caseId: String, updatedAt: Long = System.currentTimeMillis())

    // Statistics Queries
    @Query("SELECT COUNT(*) FROM missing_cases WHERE status IN ('Active', 'Resolved')")
    fun getTotalVerifiedCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM missing_cases WHERE status = 'Active'")
    fun getActiveCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM missing_cases WHERE status = 'Resolved'")
    fun getResolvedCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM missing_cases WHERE createdAt >= :cutoffTime")
    fun getRecentReportsCountFlow(cutoffTime: Long): Flow<Int>

    // Sightings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSighting(sighting: SightingEntity)

    @Query("SELECT * FROM sightings ORDER BY createdAt DESC")
    fun getAllSightingsFlow(): Flow<List<SightingEntity>>

    @Query("SELECT * FROM sightings WHERE caseId = :caseId ORDER BY createdAt DESC")
    fun getSightingsByCaseFlow(caseId: String): Flow<List<SightingEntity>>

    @Query("UPDATE sightings SET isVerified = :verified, adminNotes = :notes WHERE sightingId = :sightingId")
    suspend fun updateSightingVerification(sightingId: String, verified: Boolean, notes: String)

    // Notifications
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE recipientUid = :uid OR recipientUid = 'ALL' ORDER BY timestamp DESC")
    fun getNotificationsFlow(uid: String): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)
}
