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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TrustBlue
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitSightingScreen(
    caseId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCases by viewModel.activeCases.collectAsState()
    val targetCase = activeCases.find { it.caseId == caseId }

    var sightingDate by remember { mutableStateOf("16 Oct 2026") }
    var sightingTime by remember { mutableStateOf("05:00 PM") }
    var selectedProvince by remember { mutableStateOf(targetCase?.lastSeenProvince ?: "Punjab") }
    var sightingCity by remember { mutableStateOf(targetCase?.lastSeenCity ?: "") }
    var sightingLocation by remember { mutableStateOf("") }
    var sightingDescription by remember { mutableStateOf("") }
    var informantName by remember { mutableStateOf("") }
    var informantPhone by remember { mutableStateOf("") }

    var validationError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var generatedSightingId by remember { mutableStateOf("") }

    val provinces = listOf(
        "Punjab",
        "Sindh",
        "Khyber Pakhtunkhwa",
        "Balochistan",
        "Islamabad Capital Territory",
        "Gilgit-Baltistan",
        "Azad Jammu & Kashmir"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Submit Confidential Sighting",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Case Link Header
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = TrustBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Related Case: ${targetCase?.publicDisplayName ?: "Case $caseId"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NavyDark
                        )
                        Text(
                            text = "Reference: $caseId",
                            fontSize = 12.sp,
                            color = TrustBlue
                        )
                    }
                }
            }

            // Confidentiality Notice
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEF3C7)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF92400E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confidential Lead: Sighting reports are NEVER published publicly. They are routed securely to authorized case handlers, CPWB, and investigating officers.",
                        fontSize = 11.sp,
                        color = Color(0xFF92400E),
                        lineHeight = 15.sp
                    )
                }
            }

            if (validationError != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = validationError ?: "",
                        color = Color(0xFFB91C1C),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Date & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = sightingDate,
                    onValueChange = { sightingDate = it },
                    label = { Text("Sighting Date *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = sightingTime,
                    onValueChange = { sightingTime = it },
                    label = { Text("Approx Time *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }

            // Province
            var provinceExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = provinceExpanded,
                onExpandedChange = { provinceExpanded = !provinceExpanded }
            ) {
                OutlinedTextField(
                    value = selectedProvince,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Province *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinceExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenu(
                    expanded = provinceExpanded,
                    onDismissRequest = { provinceExpanded = false }
                ) {
                    provinces.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p) },
                            onClick = {
                                selectedProvince = p
                                provinceExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = sightingCity,
                onValueChange = {
                    sightingCity = it
                    validationError = null
                },
                label = { Text("City / Town *") },
                placeholder = { Text("e.g. Rawalpindi, Saddar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = sightingLocation,
                onValueChange = {
                    sightingLocation = it
                    validationError = null
                },
                label = { Text("Specific Landmark / Street / Bus Stop *") },
                placeholder = { Text("e.g. Near Metro Station bus stop or Liberty Market entrance") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = sightingDescription,
                onValueChange = {
                    sightingDescription = it
                    validationError = null
                },
                label = { Text("Sighting Details & Observations *") },
                placeholder = { Text("Describe child's appearance, clothing, physical state, whether accompanied by anyone, vehicle details, etc.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Text(
                text = "Optional Informant Contact",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = informantName,
                    onValueChange = { informantName = it },
                    label = { Text("Your Name (Optional)") },
                    placeholder = { Text("Anonymous") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = informantPhone,
                    onValueChange = { informantPhone = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("0300-XXXXXXX") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (sightingCity.isBlank()) {
                        validationError = "Please enter the city where the sighting occurred"
                        return@Button
                    }
                    if (sightingLocation.isBlank()) {
                        validationError = "Please provide the specific landmark or location"
                        return@Button
                    }
                    if (sightingDescription.isBlank()) {
                        validationError = "Please write observations to help officers verify"
                        return@Button
                    }
                    validationError = null

                    viewModel.submitSighting(
                        caseId = caseId,
                        caseChildName = targetCase?.publicDisplayName ?: "Child",
                        submittedBy = informantName.ifBlank { "Citizen Informant" },
                        contactPhone = informantPhone,
                        sightingDate = sightingDate,
                        sightingTime = sightingTime,
                        sightingProvince = selectedProvince,
                        sightingCity = sightingCity,
                        sightingLocation = sightingLocation,
                        sightingDescription = sightingDescription,
                        onSuccess = { sId ->
                            generatedSightingId = sId
                            showSuccessDialog = true
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AmberAccent, contentColor = NavyDark)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
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
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text("Sighting Received Securely", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Thank you for your vigilance. Your confidential sighting has been submitted with Reference ID: $generatedSightingId.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Case handlers and police officers have been alerted to review the details and verify camera feeds in $sightingCity.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TrustBlue)
                ) {
                    Text("Done")
                }
            }
        )
    }
}
