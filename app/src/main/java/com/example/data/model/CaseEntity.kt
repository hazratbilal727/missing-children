package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missing_cases")
data class CaseEntity(
    @PrimaryKey
    val caseId: String, // e.g. "MC-PK-2026-1042"
    val ownerUid: String,
    val ownerName: String,
    val ownerPhone: String, // Kept strictly private
    val ownerRelationship: String, // "Father", "Mother", "Guardian"
    val policeReportNumber: String = "", // FIR / Police report reference
    val publicDisplayName: String,
    val legalFullName: String = "", // Kept private
    val age: Int,
    val gender: String, // "Male", "Female", "Other"
    val photoUrl: String = "",
    val photoApprovedForPublic: Boolean = true,
    val height: String = "",
    val clothing: String = "",
    val distinguishingFeatures: String = "",
    val missingDate: String,
    val missingTime: String = "",
    val lastSeenProvince: String,
    val lastSeenCity: String,
    val lastSeenDistrict: String = "",
    val circumstances: String = "",
    val status: String, // "Under Review", "Active", "Resolved", "Rejected", "Archived"
    val rejectionReason: String = "",
    val resolutionNotes: String = "",
    val resolvedDate: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val sightingsCount: Int = 0
)

@Entity(tableName = "sightings")
data class SightingEntity(
    @PrimaryKey
    val sightingId: String,
    val caseId: String,
    val caseChildName: String,
    val submittedBy: String = "Anonymous Citizen",
    val contactPhone: String = "",
    val sightingDate: String,
    val sightingTime: String,
    val sightingProvince: String,
    val sightingCity: String,
    val sightingLocation: String,
    val sightingDescription: String,
    val isVerified: Boolean = false,
    val adminNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val recipientUid: String,
    val title: String,
    val message: String,
    val relatedCaseId: String = "",
    val type: String, // "status_change", "new_case", "sighting_received"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class DashboardStats(
    val totalVerifiedReports: Int = 0,
    val activeMissingCases: Int = 0,
    val successfullyResolvedCases: Int = 0,
    val recentlyReportedCases: Int = 0
)

enum class UserRole(val displayName: String) {
    PUBLIC("Public Citizen"),
    GUARDIAN("Parent / Guardian"),
    ADMIN("Authorized Moderator / Admin")
}

data class AppUser(
    val uid: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: UserRole = UserRole.GUARDIAN,
    val isAuthenticated: Boolean = true
)
