package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.dialEmergencyNumber
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TrustBlue

data class HelplineContact(
    val title: String,
    val subtitle: String,
    val number: String,
    val isPrimary: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyHelplinesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val helplines = listOf(
        HelplineContact(
            title = "Zainab Alert Helpline (Central Agency)",
            subtitle = "Ministry of Human Rights & National Child Abduction Response",
            number = "1121",
            isPrimary = true
        ),
        HelplineContact(
            title = "Pakistan Police Emergency",
            subtitle = "Immediate 24/7 First Response & FIR Registration",
            number = "15",
            isPrimary = true
        ),
        HelplineContact(
            title = "Child Protection & Welfare Bureau (CPWB)",
            subtitle = "Shelter, Legal Protection, and Recovery Support",
            number = "1121",
            isPrimary = false
        ),
        HelplineContact(
            title = "National Child Abuse Helpline",
            subtitle = "Child Rights Support & Protection Services",
            number = "1098",
            isPrimary = false
        ),
        HelplineContact(
            title = "Edhi Foundation Missing Welfare",
            subtitle = "Nationwide Lost Children Shelters & Ambulances",
            number = "115",
            isPrimary = false
        ),
        HelplineContact(
            title = "Chhipa Welfare Association",
            subtitle = "Emergency Ambulance & Shelter Bureau",
            number = "1020",
            isPrimary = false
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Emergency Helplines Directory",
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF92400E),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "In case of any emergency, abduction, or sighting in Pakistan, tap any helpline below to connect immediately.",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            items(helplines) { h ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (h.isPrimary) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = h.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (h.isPrimary) NavyDark else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = h.subtitle,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { dialEmergencyNumber(context, h.number) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (h.isPrimary) AmberAccent else TrustBlue,
                                contentColor = if (h.isPrimary) NavyDark else Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = h.number,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
