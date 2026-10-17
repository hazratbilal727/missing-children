package com.example

import com.example.data.model.CaseEntity
import com.example.data.model.DashboardStats
import com.example.data.model.UserRole
import com.example.util.AppLanguage
import com.example.util.AppStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MissingChildrenUnitTest {

    @Test
    fun testLanguageTranslations() {
        val englishTitle = AppStrings.get("app_title", AppLanguage.ENGLISH)
        val urduTitle = AppStrings.get("app_title", AppLanguage.URDU)

        assertEquals("Missing Children", englishTitle)
        assertEquals("گمشدہ بچے", urduTitle)

        // RTL verification
        assertEquals(androidx.compose.ui.unit.LayoutDirection.Rtl, AppLanguage.URDU.layoutDirection)
        assertEquals(androidx.compose.ui.unit.LayoutDirection.Ltr, AppLanguage.ENGLISH.layoutDirection)
    }

    @Test
    fun testCaseModelCreation() {
        val caseEntity = CaseEntity(
            caseId = "MC-PK-2026-1042",
            ownerUid = "uid_123",
            ownerName = "Muhammad Tariq",
            ownerPhone = "0300-1234567",
            ownerRelationship = "Father",
            publicDisplayName = "Ali Raza",
            legalFullName = "Muhammad Ali Raza",
            age = 8,
            gender = "Male",
            missingDate = "14 Oct 2026",
            lastSeenProvince = "Punjab",
            lastSeenCity = "Lahore",
            circumstances = "Near school bus stop",
            status = "Under Review"
        )

        assertEquals("MC-PK-2026-1042", caseEntity.caseId)
        assertEquals("Under Review", caseEntity.status)
        assertEquals(8, caseEntity.age)
        assertEquals("Punjab", caseEntity.lastSeenProvince)
    }

    @Test
    fun testDashboardStats() {
        val stats = DashboardStats(
            totalVerifiedReports = 120,
            activeMissingCases = 15,
            successfullyResolvedCases = 105,
            recentlyReportedCases = 3
        )

        assertEquals(120, stats.totalVerifiedReports)
        assertEquals(15, stats.activeMissingCases)
        assertEquals(105, stats.successfullyResolvedCases)
        assertTrue(stats.successfullyResolvedCases > stats.activeMissingCases)
    }

    @Test
    fun testUserRoleDefinitions() {
        val roles = UserRole.values()
        assertTrue(roles.contains(UserRole.PUBLIC))
        assertTrue(roles.contains(UserRole.GUARDIAN))
        assertTrue(roles.contains(UserRole.ADMIN))
    }
}
