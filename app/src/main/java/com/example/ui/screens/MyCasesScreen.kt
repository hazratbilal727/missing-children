package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaseEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberDark
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TrustBlue
import com.example.ui.viewmodel.MainViewModel
import com.example.util.stringRes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCasesScreen(
    viewModel: MainViewModel,
    onNavigateToReport: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val myCases by viewModel.myCases.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringRes("nav_my_cases"),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToReport,
                containerColor = AmberAccent,
                contentColor = NavyDark,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                text = { Text("New Report", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TrustBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Authenticated Guardian Session",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Logged in as ${currentUser.fullName} (${currentUser.phone})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (myCases.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = stringRes("my_cases_empty"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "If a child is missing, report immediately to initiate official verification.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onNavigateToReport,
                                colors = ButtonDefaults.buttonColors(containerColor = TrustBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Submit Report Now")
                            }
                        }
                    }
                }
            } else {
                items(myCases, key = { it.caseId }) { caseItem ->
                    MyCaseItemCard(
                        caseItem = caseItem,
                        onViewDetails = onNavigateToDetails,
                        onRequestClosure = {
                            viewModel.resolveCase(caseItem.caseId, "Guardian confirmed child found and reunited.")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MyCaseItemCard(
    caseItem: CaseEntity,
    onViewDetails: (String) -> Unit,
    onRequestClosure: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        .format(Date(caseItem.createdAt))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetails(caseItem.caseId) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = caseItem.caseId,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrustBlue
                )
                StatusBadge(status = caseItem.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = caseItem.publicDisplayName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Missing: ${caseItem.missingDate} (${caseItem.lastSeenCity}, ${caseItem.lastSeenProvince})",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Submitted on $dateStr",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Verification Timeline Visualizer
            CaseProgressTimeline(status = caseItem.status)

            if (caseItem.rejectionReason.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Moderator Note: ${caseItem.rejectionReason}",
                        color = Color(0xFFB91C1C),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onViewDetails(caseItem.caseId) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "View Case File", fontSize = 12.sp)
                }

                if (caseItem.status == "Active" || caseItem.status == "Under Review") {
                    Button(
                        onClick = onRequestClosure,
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Child Found / Close", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CaseProgressTimeline(status: String) {
    val steps = listOf("Submitted", "Under Review", "Active Feed", "Resolved")
    val currentIndex = when (status) {
        "Under Review" -> 1
        "Active" -> 2
        "Resolved" -> 3
        else -> 0
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val isCompleted = index <= currentIndex
            val color = if (isCompleted) TrustBlue else Color(0xFFCBD5E1)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    fontSize = 9.sp,
                    fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
