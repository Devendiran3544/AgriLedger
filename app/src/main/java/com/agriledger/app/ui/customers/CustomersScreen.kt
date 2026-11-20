package com.agriledger.app.ui.customers

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.CustomerWithSummary
import com.agriledger.app.ui.components.AgriLedgerCard
import com.agriledger.app.ui.components.AgriLedgerTopBar
import com.agriledger.app.ui.components.EmptyStateView
import com.agriledger.app.ui.components.SearchAndFilterBar
import com.agriledger.app.ui.theme.AccentGreen
import com.agriledger.app.ui.theme.AccentGreenLight
import com.agriledger.app.ui.theme.ErrorCrimson
import com.agriledger.app.ui.theme.PrimaryForest
import com.agriledger.app.ui.theme.SuccessGreen
import com.agriledger.app.utils.Formatters

@Composable
fun CustomersScreen(
    viewModel: CustomerViewModel,
    onCustomerClick: (Long) -> Unit
) {
    val customersWithSummary by viewModel.customersWithSummary.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val profile = viewModel.businessProfile.collectAsState().value ?: BusinessProfile()
    val currency = profile.currencySymbol
    val context = LocalContext.current

    var showAddCustomerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AgriLedgerTopBar(
                title = "Customers",
                subtitle = "${customersWithSummary.size} farmers registered"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCustomerDialog = true },
                containerColor = PrimaryForest,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search & Filter Section
            SearchAndFilterBar(
                searchQuery = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholderText = "Search by name, phone, village...",
                filterOptions = listOf("All", "Has Balance", "Cleared"),
                selectedFilter = selectedFilter,
                onFilterSelected = viewModel::onFilterChange,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )

            if (customersWithSummary.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.People,
                    title = if (searchQuery.isNotEmpty()) "No matching customers" else "No customers added yet",
                    description = if (searchQuery.isNotEmpty()) "Try adjusting your search terms" else "Start adding farmers and clients to track agricultural billing and payments.",
                    actionButtonText = if (searchQuery.isEmpty()) "Add First Customer" else null,
                    onActionClick = { showAddCustomerDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(customersWithSummary) { item ->
                        CustomerCard(
                            item = item,
                            currencySymbol = currency,
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${item.customer.phone}")
                                }
                                context.startActivity(intent)
                            },
                            onClick = { onCustomerClick(item.customer.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showAddCustomerDialog) {
        AddEditCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onSave = { customer ->
                viewModel.addCustomer(customer) {
                    showAddCustomerDialog = false
                }
            }
        )
    }
}

@Composable
private fun CustomerCard(
    item: CustomerWithSummary,
    currencySymbol: String,
    onCall: () -> Unit,
    onClick: () -> Unit
) {
    AgriLedgerCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AccentGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryForest,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.customer.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${item.customer.village.ifBlank { "Farmer" }} • ${item.customer.phone}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AccentGreenLight)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call Farmer",
                    tint = PrimaryForest,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Metrics breakdown row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Serviced",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = Formatters.formatAcres(item.totalAcresServiced),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Column {
                Text(
                    text = "Total Billed",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = Formatters.formatCurrency(item.totalBilled, currencySymbol),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Balance Due",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = Formatters.formatCurrency(item.totalPending, currencySymbol),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (item.totalPending > 0) ErrorCrimson else SuccessGreen
                    )
                )
            }
        }
    }
}
