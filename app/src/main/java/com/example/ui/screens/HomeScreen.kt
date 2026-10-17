package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.DashboardStatItem
import com.example.ui.components.EmergencyAlertBanner
import com.example.ui.components.PublicCaseCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberDark
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TrustBlue
import com.example.ui.viewmodel.MainViewModel
import com.example.util.AppLanguage
import com.example.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToReport: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToSighting: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val cases by viewModel.filteredActiveCases.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedProvince by viewModel.selectedProvince.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val unreadNotificationsCount = notifications.count { !it.isRead }

    val provinces = listOf(
        "All",
        "Punjab",
        "Sindh",
        "Khyber Pakhtunkhwa",
        "Balochistan",
        "Islamabad Capital Territory"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Missing Children Pakistan",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text(
                                text = stringRes("app_title"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Pakistan • ${currentUser.role.displayName}",
                                fontSize = 11.sp,
                                color = AmberAccent
                            )
                        }
                    }
                },
                actions = {
                    // Language Switcher
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .padding(end = 4.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        onClick = { viewModel.toggleLanguage() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Switch Language",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (currentLang == AppLanguage.ENGLISH) "اردو" else "EN",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Notifications Bell
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(containerColor = AmberAccent, contentColor = NavyDark) {
                                        Text(text = unreadNotificationsCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Emergency Dialer Banner
            item {
                EmergencyAlertBanner()
            }

            // Report Missing Child Primary CTA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringRes("report_missing_child"),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Submit verified report directly to authorities and public feed",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = onNavigateToReport,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberAccent,
                                contentColor = NavyDark
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAlert,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Report",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Dashboard Statistics (Calculated Live from Database)
            item {
                Column {
                    Text(
                        text = "National Overview Statistics",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardStatItem(
                            title = stringRes("stats_verified"),
                            count = stats.totalVerifiedReports,
                            icon = Icons.Default.Shield,
                            accentColor = TrustBlue,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardStatItem(
                            title = stringRes("stats_active"),
                            count = stats.activeMissingCases,
                            icon = Icons.Default.Warning,
                            accentColor = AmberDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardStatItem(
                            title = stringRes("stats_resolved"),
                            count = stats.successfullyResolvedCases,
                            icon = Icons.Default.CheckCircle,
                            accentColor = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardStatItem(
                            title = stringRes("stats_recent"),
                            count = stats.recentlyReportedCases,
                            icon = Icons.Default.AddAlert,
                            accentColor = Color(0xFF8B5CF6),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Search Bar & Province Filters
            item {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = stringRes("search_hint"), fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TrustBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Province Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        provinces.forEach { province ->
                            val isSelected = selectedProvince == province
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectedProvince.value = province },
                                label = {
                                    Text(
                                        text = if (province == "All") stringRes("all_provinces") else province,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TrustBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Feed Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringRes("feed_title"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${cases.size} Active Cases",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Empty State
            if (cases.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Active Missing Cases Found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try clearing filters or search terms.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(cases, key = { it.caseId }) { caseItem ->
                    PublicCaseCard(
                        caseItem = caseItem,
                        onViewDetails = onNavigateToDetails,
                        onHaveInformation = onNavigateToSighting
                    )
                }
            }
        }
    }
}
