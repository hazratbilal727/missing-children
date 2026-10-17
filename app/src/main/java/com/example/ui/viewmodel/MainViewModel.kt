package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppUser
import com.example.data.model.CaseEntity
import com.example.data.model.DashboardStats
import com.example.data.model.NotificationEntity
import com.example.data.model.SightingEntity
import com.example.data.model.UserRole
import com.example.data.repository.MissingChildrenRepository
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MissingChildrenRepository

    val activeCases: StateFlow<List<CaseEntity>>
    val resolvedCases: StateFlow<List<CaseEntity>>
    val underReviewCases: StateFlow<List<CaseEntity>>
    val allSightings: StateFlow<List<SightingEntity>>
    val dashboardStats: StateFlow<DashboardStats>

    private val _currentUser = MutableStateFlow(
        AppUser(
            uid = "current_user_1",
            fullName = "Hazrat Bilal",
            email = "hazratbilalvlog@gmail.com",
            phone = "0300-8889999",
            role = UserRole.GUARDIAN,
            isAuthenticated = true
        )
    )
    val currentUser: StateFlow<AppUser> = _currentUser.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    val myCases: StateFlow<List<CaseEntity>>
    val notifications: StateFlow<List<NotificationEntity>>

    // Search and filter state
    val searchQuery = MutableStateFlow("")
    val selectedProvince = MutableStateFlow("All")
    val selectedCity = MutableStateFlow("")
    val minAge = MutableStateFlow<Int?>(null)
    val maxAge = MutableStateFlow<Int?>(null)

    // Filtered public feed
    val filteredActiveCases: StateFlow<List<CaseEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).missingChildrenDao()
        repository = MissingChildrenRepository(dao)

        activeCases = repository.activeCases.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        resolvedCases = repository.resolvedCases.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        underReviewCases = repository.underReviewCases.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allSightings = repository.allSightings.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        dashboardStats = repository.dashboardStats.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DashboardStats()
        )

        myCases = repository.getMyCases("current_user_1").stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        notifications = repository.getNotifications("current_user_1").stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        filteredActiveCases = combine(
            activeCases,
            searchQuery,
            selectedProvince
        ) { cases, query, province ->
            cases.filter { caseItem ->
                val matchesQuery = query.isBlank() ||
                    caseItem.publicDisplayName.contains(query, ignoreCase = true) ||
                    caseItem.lastSeenCity.contains(query, ignoreCase = true) ||
                    caseItem.lastSeenProvince.contains(query, ignoreCase = true) ||
                    caseItem.caseId.contains(query, ignoreCase = true)

                val matchesProvince = province == "All" ||
                    caseItem.lastSeenProvince.equals(province, ignoreCase = true)

                matchesQuery && matchesProvince
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.ENGLISH) {
            AppLanguage.URDU
        } else {
            AppLanguage.ENGLISH
        }
    }

    fun setUserRole(role: UserRole) {
        _currentUser.value = _currentUser.value.copy(role = role)
    }

    fun toggleRole() {
        val newRole = when (_currentUser.value.role) {
            UserRole.GUARDIAN -> UserRole.ADMIN
            UserRole.ADMIN -> UserRole.PUBLIC
            UserRole.PUBLIC -> UserRole.GUARDIAN
        }
        setUserRole(newRole)
    }

    fun submitReport(
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
        circumstances: String,
        ownerRelationship: String,
        ownerPhone: String,
        policeReportNumber: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val caseId = repository.submitReport(
                ownerUid = user.uid,
                ownerName = user.fullName,
                ownerPhone = ownerPhone.ifBlank { user.phone },
                ownerRelationship = ownerRelationship,
                policeReportNumber = policeReportNumber,
                publicDisplayName = publicDisplayName,
                legalFullName = legalFullName,
                age = age,
                gender = gender,
                height = height,
                clothing = clothing,
                distinguishingFeatures = distinguishingFeatures,
                missingDate = missingDate,
                missingTime = missingTime,
                lastSeenProvince = lastSeenProvince,
                lastSeenCity = lastSeenCity,
                lastSeenDistrict = lastSeenDistrict,
                circumstances = circumstances
            )
            onSuccess(caseId)
        }
    }

    fun submitSighting(
        caseId: String,
        caseChildName: String,
        submittedBy: String,
        contactPhone: String,
        sightingDate: String,
        sightingTime: String,
        sightingProvince: String,
        sightingCity: String,
        sightingLocation: String,
        sightingDescription: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val sightingId = repository.submitSighting(
                caseId = caseId,
                caseChildName = caseChildName,
                submittedBy = submittedBy,
                contactPhone = contactPhone,
                sightingDate = sightingDate,
                sightingTime = sightingTime,
                sightingProvince = sightingProvince,
                sightingCity = sightingCity,
                sightingLocation = sightingLocation,
                sightingDescription = sightingDescription
            )
            onSuccess(sightingId)
        }
    }

    fun approveCase(caseId: String) {
        viewModelScope.launch {
            repository.approveCase(caseId)
        }
    }

    fun rejectCase(caseId: String, reason: String) {
        viewModelScope.launch {
            repository.rejectCase(caseId, reason)
        }
    }

    fun resolveCase(caseId: String, resolutionNotes: String) {
        viewModelScope.launch {
            repository.markCaseResolved(caseId, resolutionNotes)
        }
    }

    fun markSightingVerified(sightingId: String, verified: Boolean, notes: String) {
        viewModelScope.launch {
            repository.markSightingVerified(sightingId, verified, notes)
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }
}
