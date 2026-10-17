package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PublicCaseCard
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TrustBlue
import com.example.ui.viewmodel.MainViewModel
import com.example.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingCasesScreen(
    viewModel: MainViewModel,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToSighting: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCases by viewModel.activeCases.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedProvince by remember { mutableStateOf("All") }

    val provinces = listOf(
        "All",
        "Punjab",
        "Sindh",
        "Khyber Pakhtunkhwa",
        "Balochistan",
        "Islamabad Capital Territory"
    )

    val filteredList = activeCases.filter { caseItem ->
        val matchesQuery = searchQuery.isBlank() ||
            caseItem.publicDisplayName.contains(searchQuery, ignoreCase = true) ||
            caseItem.lastSeenCity.contains(searchQuery, ignoreCase = true) ||
            caseItem.caseId.contains(searchQuery, ignoreCase = true)

        val matchesProvince = selectedProvince == "All" ||
            caseItem.lastSeenProvince.equals(selectedProvince, ignoreCase = true)

        matchesQuery && matchesProvince
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringRes("nav_pending"),
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
        ) {
            // Search & Filter header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Search active cases by child name or city...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                            onClick = { selectedProvince = province },
                            label = { Text(text = if (province == "All") stringRes("all_provinces") else province) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TrustBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Displaying ${filteredList.size} verified active public cases",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                items(filteredList, key = { it.caseId }) { caseItem ->
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
