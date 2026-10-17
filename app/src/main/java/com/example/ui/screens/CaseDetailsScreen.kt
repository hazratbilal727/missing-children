package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaseEntity
import com.example.ui.components.ChildSilhouettePlaceholder
import com.example.ui.components.EmergencyAlertBanner
import com.example.ui.components.StatusBadge
import com.example.ui.components.dialEmergencyNumber
import com.example.ui.components.shareCaseDetails
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TrustBlue
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailsScreen(
    caseId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSighting: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeCases by viewModel.activeCases.collectAsState()
    val resolvedCases by viewModel.resolvedCases.collectAsState()
    val underReviewCases by viewModel.underReviewCases.collectAsState()

    val caseItem = (activeCases + resolvedCases + underReviewCases).find { it.caseId == caseId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = caseItem?.publicDisplayName ?: "Case File",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (caseItem != null) {
                        IconButton(onClick = { shareCaseDetails(context, caseItem) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        }
    ) { innerPadding ->
        if (caseItem == null) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Case not found or archived.")
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Card with Image & Basic Info
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CASE FILE: ${caseItem.caseId}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrustBlue
                            )
                            StatusBadge(status = caseItem.status)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (caseItem.status == "Resolved") {
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .background(Color(0xFFDCFCE7), RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Privacy Protected",
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            } else {
                                ChildSilhouettePlaceholder(
                                    name = caseItem.publicDisplayName,
                                    age = caseItem.age,
                                    gender = caseItem.gender,
                                    modifier = Modifier.size(90.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = if (caseItem.status == "Resolved") "Case Resolved / Child Safe" else caseItem.publicDisplayName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Age: ${caseItem.age} years • Gender: ${caseItem.gender}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (caseItem.height.isNotBlank()) {
                                    Text(
                                        text = "Height: ${caseItem.height}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (caseItem.policeReportNumber.isNotBlank()) {
                                    Text(
                                        text = "Police FIR: ${caseItem.policeReportNumber}",
                                        fontSize = 11.sp,
                                        color = TrustBlue
                                    )
                                }
                            }
                        }
                    }
                }

                // Disappearance Details
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Last Seen Information",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        DetailRow(
                            label = "Location",
                            value = "${caseItem.lastSeenCity}, ${caseItem.lastSeenProvince}" +
                                if (caseItem.lastSeenDistrict.isNotBlank()) " (${caseItem.lastSeenDistrict})" else ""
                        )
                        DetailRow(
                            label = "Missing Date & Time",
                            value = "${caseItem.missingDate} ${caseItem.missingTime}"
                        )
                        if (caseItem.clothing.isNotBlank()) {
                            DetailRow(label = "Clothing", value = caseItem.clothing)
                        }
                        if (caseItem.distinguishingFeatures.isNotBlank()) {
                            DetailRow(label = "Distinguishing Marks", value = caseItem.distinguishingFeatures)
                        }
                        if (caseItem.circumstances.isNotBlank()) {
                            DetailRow(label = "Circumstances", value = caseItem.circumstances)
                        }
                    }
                }

                // If resolved, show resolution summary
                if (caseItem.status == "Resolved" && caseItem.resolutionNotes.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Official Resolution Confirmation",
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = caseItem.resolutionNotes,
                                fontSize = 12.sp,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                }

                // Immediate danger notice
                EmergencyAlertBanner()

                // Actions: Submit Sighting & Share
                if (caseItem.status == "Active") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onNavigateToSighting(caseItem.caseId) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent, contentColor = NavyDark),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Submit Confidential Sighting",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { shareCaseDetails(context, caseItem) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Share Case Alert with Friends & Family",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}
