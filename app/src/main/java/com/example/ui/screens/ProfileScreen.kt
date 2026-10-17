package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberDark
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TrustBlue
import com.example.ui.viewmodel.MainViewModel
import com.example.util.AppLanguage
import com.example.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateToAdmin: () -> Unit,
    onNavigateToHelplines: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showSafetyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringRes("nav_profile"),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
            // User Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser.fullName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentUser.email,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Phone: ${currentUser.phone}",
                            fontSize = 12.sp,
                            color = TrustBlue
                        )
                    }
                }
            }

            // Role Switcher Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Operating Mode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentUser.role.displayName,
                                fontSize = 13.sp,
                                color = TrustBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleRole() },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent, contentColor = NavyDark)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Switch Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tip: Toggle to 'Authorized Moderator / Admin' to test the administrative verification queue, review confidential citizen sightings, and manage approvals.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Language Selector Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = TrustBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringRes("language"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = currentLang.label,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.toggleLanguage() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (currentLang == AppLanguage.ENGLISH) "اردو میں بدلیں" else "Switch to English",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Admin Portal Access (if role is admin)
            if (currentUser.role == UserRole.ADMIN) {
                Card(
                    modifier = Modifier.clickable { onNavigateToAdmin() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Moderation Dashboard",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Verify reports & inspect confidential sightings",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = AmberAccent
                        )
                    }
                }
            }

            // Safety Directory and Legal Resources
            Text(
                text = "Child Safety & Resources",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            ProfileOptionItem(
                title = stringRes("helplines"),
                subtitle = "1121 Zainab Alert, 15 Police, 115 Edhi, 1020 Chhipa",
                icon = Icons.Default.Call,
                onClick = onNavigateToHelplines
            )

            ProfileOptionItem(
                title = stringRes("safety_guide"),
                subtitle = "Zainab Alert Act 2020 legal protections and reporting guidelines",
                icon = Icons.Default.Shield,
                onClick = { showSafetyDialog = true }
            )

            ProfileOptionItem(
                title = stringRes("privacy_policy"),
                subtitle = "Child privacy, photo masking, and confidential sighting rules",
                icon = Icons.Default.Policy,
                onClick = { showPrivacyDialog = true }
            )

            ProfileOptionItem(
                title = stringRes("about_app"),
                subtitle = "Mission, verified data architecture, and contact information",
                icon = Icons.Default.Info,
                onClick = { showAboutDialog = true }
            )
        }
    }

    if (showSafetyDialog) {
        AlertDialog(
            onDismissRequest = { showSafetyDialog = false },
            title = { Text("Zainab Alert & Legal Framework", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Under the Zainab Alert, Response and Recovery Act 2020:\n\n" +
                            "• Any report of a missing or abducted child triggers immediate mandatory law enforcement action.\n" +
                            "• No 24-hour waiting period is required in Pakistan to register an FIR for a missing child.\n" +
                            "• The Child Protection and Welfare Bureau (CPWB) and Pakistan Police are connected to the central toll-free helpline 1121.\n" +
                            "• Broadcast alerts are coordinated across law enforcement, public transit, and emergency networks.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showSafetyDialog = false }) { Text("Close") }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Safeguarding & Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Missing Children Pakistan enforces strict zero-trust child privacy protections:\n\n" +
                            "1. Sensitive Personal Information: Legal full names, home addresses, school details, and parent contact details are NEVER shown publicly.\n" +
                            "2. Confidential Sightings: Tips submitted by citizens are routed only to verified officers.\n" +
                            "3. Post-Reunion Safeguard: When a child is marked resolved/reunited, public photographs and identifying markers are archived to protect the child's lifelong dignity.\n" +
                            "4. No public comments, unmoderated discussions, or direct messaging with children.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) { Text("Understood") }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Missing Children Pakistan", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Missing Children is a nationwide child protection, reporting, and recovery portal developed for Pakistan.\n\n" +
                            "Version 1.0 (PK Edition)\n" +
                            "Developed in Kotlin & Jetpack Compose\n" +
                            "Integrated with official Pakistani emergency protocols (Zainab Alert 1121 / Police 15 / CPWB).",
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
fun ProfileOptionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TrustBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
