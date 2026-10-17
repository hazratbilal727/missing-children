package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
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
fun ReportMissingChildScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onSubmissionComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()

    var currentStep by remember { mutableIntStateOf(1) }

    // Step 1: Child Info
    var legalFullName by remember { mutableStateOf("") }
    var publicDisplayName by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var heightText by remember { mutableStateOf("") }
    var clothingText by remember { mutableStateOf("") }
    var distinguishingFeatures by remember { mutableStateOf("") }

    // Step 2: Disappearance Info
    var missingDate by remember { mutableStateOf("16 Oct 2026") }
    var missingTime by remember { mutableStateOf("03:30 PM") }
    var selectedProvince by remember { mutableStateOf("Punjab") }
    var lastSeenCity by remember { mutableStateOf("") }
    var lastSeenDistrict by remember { mutableStateOf("") }
    var circumstances by remember { mutableStateOf("") }

    // Step 3: Guardian Info
    var guardianName by remember { mutableStateOf(currentUser.fullName) }
    var relationship by remember { mutableStateOf("Father") }
    var guardianPhone by remember { mutableStateOf(currentUser.phone) }
    var policeReportNumber by remember { mutableStateOf("") }

    // Step 4: Consents
    var accuracyConfirmed by remember { mutableStateOf(false) }
    var photoConsent by remember { mutableStateOf(false) }
    var privacyAccepted by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var generatedCaseId by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val provinces = listOf(
        "Punjab",
        "Sindh",
        "Khyber Pakhtunkhwa",
        "Balochistan",
        "Islamabad Capital Territory",
        "Gilgit-Baltistan",
        "Azad Jammu & Kashmir"
    )

    val relationships = listOf("Father", "Mother", "Guardian / Relative", "Legal Representative")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Report Missing Child", fontWeight = FontWeight.Bold, color = Color.White)
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
        ) {
            // Wizard Step Indicator
            WizardStepIndicator(currentStep = currentStep)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

                when (currentStep) {
                    1 -> {
                        // Step 1: Child Information
                        Text(
                            text = "Step 1: Child Details",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = TrustBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Privacy Safeguard: Legal full name is kept strictly private for police and official verification. Only approved display name appears publicly.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1E40AF),
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        OutlinedTextField(
                            value = publicDisplayName,
                            onValueChange = {
                                publicDisplayName = it
                                validationError = null
                            },
                            label = { Text("Public Display Name (e.g. Ali Raza) *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = legalFullName,
                            onValueChange = { legalFullName = it },
                            label = { Text("Legal Full Name (Private / Official Record) *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = ageText,
                                onValueChange = {
                                    ageText = it.filter { char -> char.isDigit() }
                                    validationError = null
                                },
                                label = { Text("Age (Years) *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { heightText = it },
                                label = { Text("Height (e.g. 125 cm)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        // Gender Selection
                        Text(text = "Gender *", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf("Male", "Female", "Other").forEach { g ->
                                val isSelected = selectedGender == g
                                OutlinedButton(
                                    onClick = { selectedGender = g },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) TrustBlue else Color.Transparent,
                                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Text(text = g, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = clothingText,
                            onValueChange = { clothingText = it },
                            label = { Text("Clothing Last Worn *") },
                            placeholder = { Text("e.g. Blue uniform shirt, black school shoes") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = distinguishingFeatures,
                            onValueChange = { distinguishingFeatures = it },
                            label = { Text("Distinguishing Features") },
                            placeholder = { Text("e.g. Birthmark on right hand, wears glasses") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    2 -> {
                        // Step 2: Disappearance Information
                        Text(
                            text = "Step 2: Disappearance Information",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = missingDate,
                                onValueChange = { missingDate = it },
                                label = { Text("Date Last Seen *") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = missingTime,
                                onValueChange = { missingTime = it },
                                label = { Text("Approx Time") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        // Province Dropdown
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = lastSeenCity,
                                onValueChange = {
                                    lastSeenCity = it
                                    validationError = null
                                },
                                label = { Text("City *") },
                                placeholder = { Text("e.g. Lahore") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = lastSeenDistrict,
                                onValueChange = { lastSeenDistrict = it },
                                label = { Text("District / Area") },
                                placeholder = { Text("e.g. Gulberg") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        OutlinedTextField(
                            value = circumstances,
                            onValueChange = { circumstances = it },
                            label = { Text("Circumstances of Disappearance *") },
                            placeholder = { Text("Provide details: last seen walking from school, bus stop, playground, etc.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    3 -> {
                        // Step 3: Guardian Details
                        Text(
                            text = "Step 3: Parent / Guardian Information",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFF92400E),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Guardian identity and contact numbers are strictly confidential and shared only with verified investigation officers (CPWB / Police).",
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E),
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        OutlinedTextField(
                            value = guardianName,
                            onValueChange = {
                                guardianName = it
                                validationError = null
                            },
                            label = { Text("Parent / Guardian Full Legal Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Relationship dropdown
                        var relExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = relExpanded,
                            onExpandedChange = { relExpanded = !relExpanded }
                        ) {
                            OutlinedTextField(
                                value = relationship,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Relationship to Child *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = relExpanded,
                                onDismissRequest = { relExpanded = false }
                            ) {
                                relationships.forEach { r ->
                                    DropdownMenuItem(
                                        text = { Text(r) },
                                        onClick = {
                                            relationship = r
                                            relExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = guardianPhone,
                            onValueChange = {
                                guardianPhone = it
                                validationError = null
                            },
                            label = { Text("Verified Mobile Number *") },
                            placeholder = { Text("e.g. 0300-1234567") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = policeReportNumber,
                            onValueChange = { policeReportNumber = it },
                            label = { Text("Police FIR / Complaint Number (if registered)") },
                            placeholder = { Text("e.g. FIR-112/26 Gulberg Police") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    4 -> {
                        // Step 4: Review and Submit
                        Text(
                            text = "Step 4: Review & Legal Declarations",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Case Summary",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrustBlue
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Child: $publicDisplayName (Age: $ageText, $selectedGender)", fontSize = 13.sp)
                                Text(text = "Last Seen: $lastSeenCity, $selectedProvince on $missingDate", fontSize = 13.sp)
                                Text(text = "Clothing: $clothingText", fontSize = 13.sp)
                                Text(text = "Guardian: $guardianName ($relationship)", fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Legal Consents
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { accuracyConfirmed = !accuracyConfirmed }
                        ) {
                            Checkbox(checked = accuracyConfirmed, onCheckedChange = { accuracyConfirmed = it })
                            Text(
                                text = "I solemnly affirm that the information provided is accurate and true to the best of my knowledge.",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { photoConsent = !photoConsent }
                        ) {
                            Checkbox(checked = photoConsent, onCheckedChange = { photoConsent = it })
                            Text(
                                text = "I authorize publication of approved child display details on the Missing Children public recovery portal.",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { privacyAccepted = !privacyAccepted }
                        ) {
                            Checkbox(checked = privacyAccepted, onCheckedChange = { privacyAccepted = it })
                            Text(
                                text = "I accept the Child Safeguarding and Privacy Policy under the Zainab Alert Act 2020.",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Bottom Navigation Buttons
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Previous")
                        }
                    }

                    Button(
                        onClick = {
                            when (currentStep) {
                                1 -> {
                                    if (publicDisplayName.isBlank()) {
                                        validationError = "Please enter the public display name"
                                        return@Button
                                    }
                                    if (legalFullName.isBlank()) {
                                        validationError = "Please enter the legal full name"
                                        return@Button
                                    }
                                    val age = ageText.toIntOrNull()
                                    if (age == null || age <= 0 || age > 18) {
                                        validationError = "Please enter a valid age between 1 and 18"
                                        return@Button
                                    }
                                    if (clothingText.isBlank()) {
                                        validationError = "Please specify clothing last worn"
                                        return@Button
                                    }
                                    validationError = null
                                    currentStep = 2
                                }
                                2 -> {
                                    if (lastSeenCity.isBlank()) {
                                        validationError = "Please enter the last seen city"
                                        return@Button
                                    }
                                    if (circumstances.isBlank()) {
                                        validationError = "Please describe the circumstances of disappearance"
                                        return@Button
                                    }
                                    validationError = null
                                    currentStep = 3
                                }
                                3 -> {
                                    if (guardianName.isBlank()) {
                                        validationError = "Please enter parent/guardian name"
                                        return@Button
                                    }
                                    if (guardianPhone.isBlank()) {
                                        validationError = "Please enter verified mobile number"
                                        return@Button
                                    }
                                    validationError = null
                                    currentStep = 4
                                }
                                4 -> {
                                    if (!accuracyConfirmed || !photoConsent || !privacyAccepted) {
                                        validationError = "Please accept all required legal confirmations and declarations to proceed."
                                        return@Button
                                    }
                                    validationError = null
                                    val age = ageText.toIntOrNull() ?: 8
                                    viewModel.submitReport(
                                        publicDisplayName = publicDisplayName,
                                        legalFullName = legalFullName,
                                        age = age,
                                        gender = selectedGender,
                                        height = heightText,
                                        clothing = clothingText,
                                        distinguishingFeatures = distinguishingFeatures,
                                        missingDate = missingDate,
                                        missingTime = missingTime,
                                        lastSeenProvince = selectedProvince,
                                        lastSeenCity = lastSeenCity,
                                        lastSeenDistrict = lastSeenDistrict,
                                        circumstances = circumstances,
                                        ownerRelationship = relationship,
                                        ownerPhone = guardianPhone,
                                        policeReportNumber = policeReportNumber,
                                        onSuccess = { caseId ->
                                            generatedCaseId = caseId
                                            showSuccessDialog = true
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentStep == 4) AmberAccent else TrustBlue,
                            contentColor = if (currentStep == 4) NavyDark else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (currentStep == 4) "Submit Report" else "Continue",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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
                Text(
                    text = "Report Submitted Successfully",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Your report has been submitted successfully and is awaiting verification.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF6FF),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Unique Case Reference ID:",
                                fontSize = 11.sp,
                                color = TrustBlue
                            )
                            Text(
                                text = generatedCaseId,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Authorized moderators will expedite verification. You can track real-time progress in the 'My Cases' tab.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSubmissionComplete(generatedCaseId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TrustBlue)
                ) {
                    Text("Go to My Cases")
                }
            }
        )
    }
}

@Composable
fun WizardStepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A).copy(alpha = 0.05f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val steps = listOf("Child", "Disappearance", "Guardian", "Review")
        steps.forEachIndexed { index, stepName ->
            val stepNumber = index + 1
            val isCurrent = stepNumber == currentStep
            val isDone = stepNumber < currentStep

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = when {
                                isDone -> SuccessGreen
                                isCurrent -> TrustBlue
                                else -> Color(0xFFCBD5E1)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = stepNumber.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stepName,
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) TrustBlue else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
