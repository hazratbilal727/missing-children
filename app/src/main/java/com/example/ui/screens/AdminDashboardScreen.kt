package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaseEntity
import com.example.data.model.SightingEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.ErrorLight
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessLight
import com.example.ui.theme.TrustBlue
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val underReviewCases by viewModel.underReviewCases.collectAsState()
    val activeCases by viewModel.activeCases.collectAsState()
    val allSightings by viewModel.allSightings.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending Verification (${underReviewCases.size})", "Confidential Sightings (${allSightings.size})", "Active Cases (${activeCases.size})")

    var rejectCaseTarget by remember { mutableStateOf<CaseEntity?>(null) }
    var rejectionReasonInput by remember { mutableStateOf("") }

    var resolveCaseTarget by remember { mutableStateOf<CaseEntity?>(null) }
    var resolutionNotesInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Moderation Portal",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDark)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = NavyPrimary,
                contentColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    // Under Review Cases Queue
                    if (underReviewCases.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Verification queue is empty. All submitted reports are verified.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(underReviewCases, key = { it.caseId }) { c ->
                                AdminCaseReviewCard(
                                    caseItem = c,
                                    onApprove = { viewModel.approveCase(c.caseId) },
                                    onReject = { rejectCaseTarget = c }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Confidential Sightings Lead Review
                    if (allSightings.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No citizen sightings submitted yet.")
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(allSightings, key = { it.sightingId }) { s ->
                                AdminSightingCard(
                                    sighting = s,
                                    onVerify = { verified ->
                                        viewModel.markSightingVerified(
                                            s.sightingId,
                                            verified,
                                            if (verified) "Validated by CPWB dispatch" else "Reviewed"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Active Cases (Can be resolved or closed)
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(activeCases, key = { it.caseId }) { c ->
                            AdminActiveCaseCard(
                                caseItem = c,
                                onMarkResolved = { resolveCaseTarget = c }
                            )
                        }
                    }
                }
            }
        }
    }

    // Rejection Dialog
    if (rejectCaseTarget != null) {
        AlertDialog(
            onDismissRequest = { rejectCaseTarget = null },
            title = { Text("Reject / Request Clarification", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Provide reason for rejection (shown privately to guardian ${rejectCaseTarget?.ownerName}):",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectionReasonInput,
                        onValueChange = { rejectionReasonInput = it },
                        placeholder = { Text("e.g. Police FIR reference number missing or unclear guardian document.") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val c = rejectCaseTarget
                        if (c != null && rejectionReasonInput.isNotBlank()) {
                            viewModel.rejectCase(c.caseId, rejectionReasonInput)
                            rejectCaseTarget = null
                            rejectionReasonInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Confirm Rejection")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { rejectCaseTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Resolution Dialog
    if (resolveCaseTarget != null) {
        AlertDialog(
            onDismissRequest = { resolveCaseTarget = null },
            title = { Text("Confirm Child Resolution / Reunion", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Enter official recovery & resolution details for ${resolveCaseTarget?.publicDisplayName} (${resolveCaseTarget?.caseId}):",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resolutionNotesInput,
                        onValueChange = { resolutionNotesInput = it },
                        placeholder = { Text("e.g. Safely reunited with parents through CPWB shelter verification in Lahore Cantt.") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val c = resolveCaseTarget
                        if (c != null && resolutionNotesInput.isNotBlank()) {
                            viewModel.resolveCase(c.caseId, resolutionNotesInput)
                            resolveCaseTarget = null
                            resolutionNotesInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Confirm Resolution")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { resolveCaseTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminCaseReviewCard(
    caseItem: CaseEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
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
                    text = caseItem.caseId,
                    fontWeight = FontWeight.Bold,
                    color = TrustBlue,
                    fontSize = 12.sp
                )
                StatusBadge(status = caseItem.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${caseItem.publicDisplayName} (Legal: ${caseItem.legalFullName})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Age: ${caseItem.age} • Gender: ${caseItem.gender} • Height: ${caseItem.height}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Last seen: ${caseItem.lastSeenCity}, ${caseItem.lastSeenProvince} on ${caseItem.missingDate}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Guardian: ${caseItem.ownerName} (${caseItem.ownerRelationship})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Private Phone: ${caseItem.ownerPhone}",
                        fontSize = 12.sp,
                        color = TrustBlue
                    )
                    Text(
                        text = "Police Report: ${caseItem.policeReportNumber.ifBlank { "Not provided" }}",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Circumstances: ${caseItem.circumstances}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject", color = ErrorRed)
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve & Publish", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminSightingCard(
    sighting: SightingEntity,
    onVerify: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sighting.sightingId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TrustBlue
                )
                if (sighting.isVerified) {
                    Surface(shape = RoundedCornerShape(8.dp), color = SuccessLight) {
                        Text(
                            text = "Verified Lead",
                            color = SuccessGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Target Case: ${sighting.caseChildName} (${sighting.caseId})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Seen at: ${sighting.sightingLocation}, ${sighting.sightingCity} on ${sighting.sightingDate} ${sighting.sightingTime}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFEF3C7).copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Description: ${sighting.sightingDescription}",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (sighting.contactPhone.isNotBlank()) {
                Text(
                    text = "Informant Contact: ${sighting.submittedBy} (${sighting.contactPhone})",
                    fontSize = 11.sp,
                    color = TrustBlue,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onVerify(!sighting.isVerified) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (sighting.isVerified) Color(0xFF64748B) else SuccessGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (sighting.isVerified) "Mark Unverified" else "Mark Verified Lead", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun AdminActiveCaseCard(
    caseItem: CaseEntity,
    onMarkResolved: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = caseItem.caseId, fontSize = 11.sp, color = TrustBlue, fontWeight = FontWeight.Bold)
                Text(text = caseItem.publicDisplayName, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = "${caseItem.lastSeenCity}, ${caseItem.lastSeenProvince}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "${caseItem.sightingsCount} confidential sightings received", fontSize = 11.sp, color = AmberAccent)
            }

            Button(
                onClick = onMarkResolved,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Mark Resolved", fontSize = 12.sp)
            }
        }
    }
}
