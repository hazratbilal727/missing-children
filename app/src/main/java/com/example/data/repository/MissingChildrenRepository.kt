package com.example.data.repository

import com.example.data.local.MissingChildrenDao
import com.example.data.model.CaseEntity
import com.example.data.model.DashboardStats
import com.example.data.model.NotificationEntity
import com.example.data.model.SightingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MissingChildrenRepository(private val dao: MissingChildrenDao) {

    val activeCases: Flow<List<CaseEntity>> = dao.getActiveCasesFlow()
    val resolvedCases: Flow<List<CaseEntity>> = dao.getResolvedCasesFlow()
    val underReviewCases: Flow<List<CaseEntity>> = dao.getUnderReviewCasesFlow()
    val allSightings: Flow<List<SightingEntity>> = dao.getAllSightingsFlow()

    fun getMyCases(ownerUid: String): Flow<List<CaseEntity>> =
        dao.getCasesByOwnerFlow(ownerUid)

    fun getCaseById(caseId: String): Flow<CaseEntity?> =
        dao.getCaseByIdFlow(caseId)

    fun getSightingsForCase(caseId: String): Flow<List<SightingEntity>> =
        dao.getSightingsByCaseFlow(caseId)

    fun getNotifications(uid: String): Flow<List<NotificationEntity>> =
        dao.getNotificationsFlow(uid)

    val dashboardStats: Flow<DashboardStats> = combine(
        dao.getTotalVerifiedCountFlow(),
        dao.getActiveCountFlow(),
        dao.getResolvedCountFlow(),
        dao.getRecentReportsCountFlow(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000)
    ) { total, active, resolved, recent ->
        DashboardStats(
            totalVerifiedReports = total,
            activeMissingCases = active,
            successfullyResolvedCases = resolved,
            recentlyReportedCases = recent
        )
    }

    suspend fun submitReport(
        ownerUid: String,
        ownerName: String,
        ownerPhone: String,
        ownerRelationship: String,
        policeReportNumber: String,
        publicDisplayName: String,
        legalFullName: String,
        age: Int,
        gender: String,
        height: String,
        clothing: String,
        distinguishingFeatures: String,
        missingDate: String,
        missingTime: String,
        lastSeenProvince: String,
        lastSeenCity: String,
        lastSeenDistrict: String,
        circumstances: String
    ): String {
        val randomNum = (1000..9999).random()
        val caseId = "MC-PK-2026-$randomNum"

        val newCase = CaseEntity(
            caseId = caseId,
            ownerUid = ownerUid,
            ownerName = ownerName,
            ownerPhone = ownerPhone,
            ownerRelationship = ownerRelationship,
            policeReportNumber = policeReportNumber,
            publicDisplayName = publicDisplayName,
            legalFullName = legalFullName,
            age = age,
            gender = gender,
            photoUrl = "",
            photoApprovedForPublic = true,
            height = height,
            clothing = clothing,
            distinguishingFeatures = distinguishingFeatures,
            missingDate = missingDate,
            missingTime = missingTime,
            lastSeenProvince = lastSeenProvince,
            lastSeenCity = lastSeenCity,
            lastSeenDistrict = lastSeenDistrict,
            circumstances = circumstances,
            status = "Under Review",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        dao.insertCase(newCase)

        // Notification for submitter
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                recipientUid = ownerUid,
                title = "Report Submitted Successfully",
                message = "Case $caseId for '$publicDisplayName' has been registered and is undergoing expedited administrative verification.",
                relatedCaseId = caseId,
                type = "status_change",
                timestamp = System.currentTimeMillis()
            )
        )

        return caseId
    }

    suspend fun submitSighting(
        caseId: String,
        caseChildName: String,
        submittedBy: String,
        contactPhone: String,
        sightingDate: String,
        sightingTime: String,
        sightingProvince: String,
        sightingCity: String,
        sightingLocation: String,
        sightingDescription: String
    ): String {
        val sightingId = "SIGHT-" + UUID.randomUUID().toString().take(8).uppercase()
        val sighting = SightingEntity(
            sightingId = sightingId,
            caseId = caseId,
            caseChildName = caseChildName,
            submittedBy = submittedBy.ifBlank { "Anonymous Citizen" },
            contactPhone = contactPhone,
            sightingDate = sightingDate,
            sightingTime = sightingTime,
            sightingProvince = sightingProvince,
            sightingCity = sightingCity,
            sightingLocation = sightingLocation,
            sightingDescription = sightingDescription,
            createdAt = System.currentTimeMillis()
        )
        dao.insertSighting(sighting)
        dao.incrementSightingsCount(caseId)

        // Find case to notify owner
        val caseObj = dao.getCaseById(caseId)
        if (caseObj != null) {
            dao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    recipientUid = caseObj.ownerUid,
                    title = "New Confidential Sighting Received",
                    message = "A sighting was reported for Case $caseId in $sightingCity. Case officers and CPWB are reviewing the details.",
                    relatedCaseId = caseId,
                    type = "sighting_received",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        return sightingId
    }

    suspend fun approveCase(caseId: String) {
        dao.updateCaseModeration(caseId, status = "Active", reason = "")
        val caseObj = dao.getCaseById(caseId)
        if (caseObj != null) {
            dao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    recipientUid = caseObj.ownerUid,
                    title = "Case Verified & Activated",
                    message = "Case $caseId for '${caseObj.publicDisplayName}' has been approved by moderators and is now visible on the public feed.",
                    relatedCaseId = caseId,
                    type = "status_change",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun rejectCase(caseId: String, reason: String) {
        dao.updateCaseModeration(caseId, status = "Rejected", reason = reason)
        val caseObj = dao.getCaseById(caseId)
        if (caseObj != null) {
            dao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    recipientUid = caseObj.ownerUid,
                    title = "Case Requires Clarification / Rejected",
                    message = "Case $caseId review note: $reason. Please update guardian documentation or contact support.",
                    relatedCaseId = caseId,
                    type = "status_change",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun markCaseResolved(caseId: String, resolutionNotes: String) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(Date())
        dao.resolveCase(caseId, resolutionNotes = resolutionNotes, resolvedDate = dateStr)

        val caseObj = dao.getCaseById(caseId)
        if (caseObj != null) {
            dao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    recipientUid = caseObj.ownerUid,
                    title = "Case Marked as Resolved",
                    message = "Alhamdulillah! Case $caseId has been officially marked as resolved. Identifying information has been transitioned to privacy-safe mode.",
                    relatedCaseId = caseId,
                    type = "status_change",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun markSightingVerified(sightingId: String, verified: Boolean, notes: String) {
        dao.updateSightingVerification(sightingId, verified, notes)
    }

    suspend fun markNotificationAsRead(id: String) {
        dao.markNotificationAsRead(id)
    }

    suspend fun seedSampleDataIfEmpty() {
        val existing = dao.getCaseById("MC-PK-2026-1042")
        if (existing != null) return

        val sampleCases = listOf(
            CaseEntity(
                caseId = "MC-PK-2026-1042",
                ownerUid = "user_demo_1",
                ownerName = "Muhammad Tariq",
                ownerPhone = "0300-1234567",
                ownerRelationship = "Father",
                policeReportNumber = "FIR-245/26 Gulberg Lahore",
                publicDisplayName = "Ali Raza",
                legalFullName = "Muhammad Ali Raza",
                age = 8,
                gender = "Male",
                photoUrl = "",
                photoApprovedForPublic = true,
                height = "125 cm",
                clothing = "Navy blue school uniform shirt, dark grey trousers, black shoes",
                distinguishingFeatures = "Small mole near left eyebrow, brown curly hair",
                missingDate = "14 Oct 2026",
                missingTime = "02:15 PM",
                lastSeenProvince = "Punjab",
                lastSeenCity = "Lahore",
                lastSeenDistrict = "Lahore Cantt",
                circumstances = "Did not return home from nearby school bus stop near Main Boulevard Gulberg. Local search initiated with Gulberg Police Station.",
                status = "Active",
                createdAt = System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000,
                updatedAt = System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000,
                sightingsCount = 2
            ),
            CaseEntity(
                caseId = "MC-PK-2026-1088",
                ownerUid = "user_demo_2",
                ownerName = "Fatima Bibi",
                ownerPhone = "0321-9876543",
                ownerRelationship = "Mother",
                policeReportNumber = "FIR-109/26 Saddar Rawalpindi",
                publicDisplayName = "Zainab Noor",
                legalFullName = "Zainab Fatima Noor",
                age = 6,
                gender = "Female",
                photoUrl = "",
                photoApprovedForPublic = true,
                height = "110 cm",
                clothing = "Pink floral shalwar kameez, white dupatta, purple sandals",
                distinguishingFeatures = "Wears small gold earrings, birthmark on right arm",
                missingDate = "15 Oct 2026",
                missingTime = "05:45 PM",
                lastSeenProvince = "Punjab",
                lastSeenCity = "Rawalpindi",
                lastSeenDistrict = "Rawalpindi Saddar",
                circumstances = "Last seen near Ayub National Park playground area while family was attending a community gathering.",
                status = "Active",
                createdAt = System.currentTimeMillis() - 1L * 24 * 60 * 60 * 1000,
                updatedAt = System.currentTimeMillis() - 1L * 24 * 60 * 60 * 1000,
                sightingsCount = 1
            ),
            CaseEntity(
                caseId = "MC-PK-2026-1120",
                ownerUid = "user_demo_3",
                ownerName = "Abdul Rahim",
                ownerPhone = "0333-5551234",
                ownerRelationship = "Uncle / Guardian",
                policeReportNumber = "FIR-512/26 Clifton Karachi",
                publicDisplayName = "Ahmed Khan",
                legalFullName = "Ahmed Bilal Khan",
                age = 11,
                gender = "Male",
                photoUrl = "",
                photoApprovedForPublic = true,
                height = "138 cm",
                clothing = "Yellow polo shirt, blue denim jeans, white sneakers",
                distinguishingFeatures = "Speaks with slight stutter, wears wire-rim prescription spectacles",
                missingDate = "13 Oct 2026",
                missingTime = "06:30 PM",
                lastSeenProvince = "Sindh",
                lastSeenCity = "Karachi",
                lastSeenDistrict = "Clifton Block 2",
                circumstances = "Separated from family during evening walk near Sea View beach front. Alert broadcasted across Edhi and Police 15.",
                status = "Active",
                createdAt = System.currentTimeMillis() - 3L * 24 * 60 * 60 * 1000,
                updatedAt = System.currentTimeMillis() - 3L * 24 * 60 * 60 * 1000,
                sightingsCount = 3
            ),
            CaseEntity(
                caseId = "MC-PK-2026-0950",
                ownerUid = "user_demo_4",
                ownerName = "Gulzar Ahmed",
                ownerPhone = "0345-7788990",
                ownerRelationship = "Father",
                policeReportNumber = "FIR-88/26 University Town",
                publicDisplayName = "Ayesha",
                legalFullName = "Ayesha Gulzar",
                age = 9,
                gender = "Female",
                photoUrl = "",
                photoApprovedForPublic = true,
                height = "120 cm",
                clothing = "Light green embroidered kurta",
                distinguishingFeatures = "Dimple on left cheek",
                missingDate = "28 Sep 2026",
                missingTime = "03:00 PM",
                lastSeenProvince = "Khyber Pakhtunkhwa",
                lastSeenCity = "Peshawar",
                lastSeenDistrict = "University Town",
                circumstances = "Safely reunited with family through Child Protection Bureau shelter verification.",
                status = "Resolved",
                resolutionNotes = "Child was located safely in Peshawar Child Protection Bureau center and safely reunited with parents after biometric and legal verification.",
                resolvedDate = "05 Oct 2026",
                createdAt = System.currentTimeMillis() - 18L * 24 * 60 * 60 * 1000,
                updatedAt = System.currentTimeMillis() - 11L * 24 * 60 * 60 * 1000,
                sightingsCount = 4
            ),
            CaseEntity(
                caseId = "MC-PK-2026-0810",
                ownerUid = "user_demo_5",
                ownerName = "Rashid Minhas",
                ownerPhone = "0312-3456789",
                ownerRelationship = "Father",
                policeReportNumber = "FIR-31/26 Zarghoon Road",
                publicDisplayName = "Bilal",
                legalFullName = "Bilal Rashid",
                age = 7,
                gender = "Male",
                photoUrl = "",
                photoApprovedForPublic = true,
                height = "115 cm",
                clothing = "Grey sweater, dark pants",
                distinguishingFeatures = "Fair complexion",
                missingDate = "15 Sep 2026",
                missingTime = "01:30 PM",
                lastSeenProvince = "Balochistan",
                lastSeenCity = "Quetta",
                lastSeenDistrict = "Quetta Cantt",
                circumstances = "Reunited with guardians via community police alert.",
                status = "Resolved",
                resolutionNotes = "Safely found by railway police at Quetta Station and transferred to Edhi shelter before family handover.",
                resolvedDate = "18 Sep 2026",
                createdAt = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000,
                updatedAt = System.currentTimeMillis() - 27L * 24 * 60 * 60 * 1000,
                sightingsCount = 2
            ),
            CaseEntity(
                caseId = "MC-PK-2026-1199",
                ownerUid = "current_user_1",
                ownerName = "Hazrat Bilal",
                ownerPhone = "0300-8889999",
                ownerRelationship = "Guardian",
                policeReportNumber = "FIR-pending",
                publicDisplayName = "Usman",
                legalFullName = "Usman Tariq",
                age = 10,
                gender = "Male",
                photoUrl = "",
                photoApprovedForPublic = true,
                height = "132 cm",
                clothing = "Red sports jersey with number 10, black shorts",
                distinguishingFeatures = "Scar on left knee",
                missingDate = "16 Oct 2026",
                missingTime = "11:00 AM",
                lastSeenProvince = "Islamabad Capital Territory",
                lastSeenCity = "Islamabad",
                lastSeenDistrict = "Sector F-10 Markaz",
                circumstances = "Report submitted by guardian, currently undergoing identity and official document verification before publication.",
                status = "Under Review",
                createdAt = System.currentTimeMillis() - 4L * 60 * 60 * 1000,
                updatedAt = System.currentTimeMillis() - 4L * 60 * 60 * 1000,
                sightingsCount = 0
            )
        )

        dao.insertCases(sampleCases)

        val sampleSightings = listOf(
            SightingEntity(
                sightingId = "SIGHT-01",
                caseId = "MC-PK-2026-1042",
                caseChildName = "Ali Raza",
                submittedBy = "Kamran (Shopkeeper)",
                contactPhone = "0302-4441122",
                sightingDate = "15 Oct 2026",
                sightingTime = "10:30 AM",
                sightingProvince = "Punjab",
                sightingCity = "Lahore",
                sightingLocation = "Near Liberty Roundabout market",
                sightingDescription = "Saw a young boy matching description with navy shirt asking for directions near the bus stop.",
                isVerified = true,
                adminNotes = "Forwarded to Investigation Officer at Gulberg Police Station.",
                createdAt = System.currentTimeMillis() - 1L * 24 * 60 * 60 * 1000
            ),
            SightingEntity(
                sightingId = "SIGHT-02",
                caseId = "MC-PK-2026-1120",
                caseChildName = "Ahmed Khan",
                submittedBy = "Citizen Informant",
                contactPhone = "",
                sightingDate = "14 Oct 2026",
                sightingTime = "08:15 PM",
                sightingProvince = "Sindh",
                sightingCity = "Karachi",
                sightingLocation = "Near Boat Basin food street",
                sightingDescription = "Child with glasses and yellow t-shirt seen resting near restaurant kiosk.",
                isVerified = false,
                adminNotes = "Edhi patrol team dispatched to check CCTV recordings.",
                createdAt = System.currentTimeMillis() - 2L * 24 * 60 * 60 * 1000
            )
        )
        for (s in sampleSightings) {
            dao.insertSighting(s)
        }

        val sampleNotifications = listOf(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                recipientUid = "current_user_1",
                title = "Verification In Progress",
                message = "Your submission for Usman (MC-PK-2026-1199) is in the verification queue. A moderator will review within 2 hours.",
                relatedCaseId = "MC-PK-2026-1199",
                type = "verification",
                timestamp = System.currentTimeMillis() - 3L * 60 * 60 * 1000
            ),
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                recipientUid = "ALL",
                title = "Zainab Alert System Active",
                message = "Direct emergency line 1121 and Police Emergency 15 are integrated for immediate nationwide broadcasts.",
                type = "new_case",
                timestamp = System.currentTimeMillis() - 24L * 60 * 60 * 1000
            )
        )
        for (n in sampleNotifications) {
            dao.insertNotification(n)
        }
    }
}
