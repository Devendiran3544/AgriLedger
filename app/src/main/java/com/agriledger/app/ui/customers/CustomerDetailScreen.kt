package com.agriledger.app.ui.customers

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SquareFoot
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.ui.components.AgriLedgerCard
import com.agriledger.app.ui.components.AgriLedgerTopBar
import com.agriledger.app.ui.components.StatusBadge
import com.agriledger.app.ui.payments.RecordPaymentDialog
import com.agriledger.app.ui.services.AddServiceJobDialog
import com.agriledger.app.ui.services.InvoicePreviewDialog
import com.agriledger.app.ui.theme.AccentGreen
import com.agriledger.app.ui.theme.AccentGreenLight
import com.agriledger.app.ui.theme.ErrorCrimson
import com.agriledger.app.ui.theme.InfoBlue
import com.agriledger.app.ui.theme.InfoBlueBg
import com.agriledger.app.ui.theme.PrimaryForest
import com.agriledger.app.ui.theme.SuccessGreen
import com.agriledger.app.ui.theme.SuccessGreenBg
import com.agriledger.app.utils.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Long,
    viewModel: CustomerViewModel,
    onNavigateBack: () -> Unit
) {
    val customer by viewModel.getCustomerById(customerId).collectAsState()
    val jobs by viewModel.getJobsForCustomer(customerId).collectAsState()
    val payments by viewModel.getPaymentsForCustomer(customerId).collectAsState()
    val profile = viewModel.businessProfile.collectAsState().value ?: BusinessProfile()
    val currency = profile.currencySymbol
    val context = LocalContext.current

    val totalAcres = jobs.sumOf { it.acres }
    val totalBilled = jobs.sumOf { it.totalAmount }
    val totalPaid = payments.sumOf { it.amount }
    val totalPending = (totalBilled - totalPaid).coerceAtLeast(0.0)

    var showEditCustomerDialog by remember { mutableStateOf(false) }
    var showAddJobDialog by remember { mutableStateOf(false) }
    var showRecordPaymentDialog by remember { mutableStateOf(false) }
    var previewJob by remember { mutableStateOf<ServiceJob?>(null) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Invoices, 1 = Payments

    if (customer == null) {
        Scaffold(
            topBar = {
                AgriLedgerTopBar(
                    title = "Customer Details",
                    showBackButton = true,
                    onBackClick = onNavigateBack
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading customer...")
            }
        }
        return
    }

    val currentCustomer = customer!!

    Scaffold(
        topBar = {
            AgriLedgerTopBar(
                title = currentCustomer.name,
                subtitle = "${currentCustomer.village} • ${currentCustomer.phone}",
                showBackButton = true,
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showEditCustomerDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Customer", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            // Customer Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentCustomer.name,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = PrimaryForest,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentCustomer.village.ifBlank { "Location not set" },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                                if (currentCustomer.totalLandAcres > 0) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.SquareFoot,
                                            contentDescription = null,
                                            tint = PrimaryForest,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Farm: ${Formatters.formatAcres(currentCustomer.totalLandAcres)}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                            // Quick Call Button
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${currentCustomer.phone}")
                                    }
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreenLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = PrimaryForest,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Call",
                                    color = PrimaryForest,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (currentCustomer.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Notes: ${currentCustomer.notes}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Customer Financial Balances Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Total Billed",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                        Text(
                                            text = Formatters.formatCurrency(totalBilled, currency),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Total Paid",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                        Text(
                                            text = Formatters.formatCurrency(totalPaid, currency),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SuccessGreen
                                            )
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Balance Due",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                        Text(
                                            text = Formatters.formatCurrency(totalPending, currency),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (totalPending > 0) ErrorCrimson else SuccessGreen
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total Land Serviced: ${Formatters.formatAcres(totalAcres)}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PrimaryForest,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = "${jobs.size} Invoices",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Customer Action Buttons: Add Bill, Record Payment, Share Statement
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showAddJobDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                            ) {
                                Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("New Bill", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showRecordPaymentDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Collect Pay", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val summary = CustomerWithSummary(
                                        customer = currentCustomer,
                                        totalAcresServiced = totalAcres,
                                        totalBilled = totalBilled,
                                        totalPaid = totalPaid,
                                        totalPending = totalPending,
                                        serviceJobsCount = jobs.size
                                    )
                                    val text = Formatters.generateCustomerStatementText(
                                        summary = summary,
                                        jobs = jobs,
                                        payments = payments,
                                        businessName = profile.businessName,
                                        currencySymbol = currency
                                    )
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, text)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Statement"))
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Tabs for Service Invoices vs Payments
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 20.dp,
                    containerColor = Color.Transparent,
                    contentColor = PrimaryForest,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PrimaryForest
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Service Invoices (${jobs.size})") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Payments (${payments.size})") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (selectedTab == 0) {
                if (jobs.isEmpty()) {
                    item {
                        Text(
                            text = "No service jobs recorded for this customer yet.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                        )
                    }
                } else {
                    items(jobs) { job ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                            AgriLedgerCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 16.dp,
                                onClick = { previewJob = job }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = job.serviceType.displayName,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = job.invoiceNumber,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = PrimaryForest,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                        }
                                        Text(
                                            text = "${Formatters.formatAcres(job.acres)} @ ${Formatters.formatCurrency(job.ratePerAcre, currency)}/Ac",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                        Text(
                                            text = Formatters.formatDate(job.serviceDate),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = Formatters.formatCurrency(job.totalAmount, currency),
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        StatusBadge(status = job.status)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (payments.isEmpty()) {
                    item {
                        Text(
                            text = "No payments recorded for this customer yet.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                        )
                    }
                } else {
                    items(payments) { payment ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                            AgriLedgerCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 16.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Payment via ${payment.paymentMode.displayName}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        if (payment.referenceNumber.isNotBlank()) {
                                            Text(
                                                text = "Ref: ${payment.referenceNumber}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                        Text(
                                            text = Formatters.formatDate(payment.paymentDate),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    Text(
                                        text = "+${Formatters.formatCurrency(payment.amount, currency)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showEditCustomerDialog) {
        AddEditCustomerDialog(
            customerToEdit = currentCustomer,
            onDismiss = { showEditCustomerDialog = false },
            onSave = { updated ->
                viewModel.updateCustomer(updated) {
                    showEditCustomerDialog = false
                }
            }
        )
    }

    if (showAddJobDialog) {
        AddServiceJobDialog(
            customers = listOf(currentCustomer),
            preselectedCustomerId = currentCustomer.id,
            currencySymbol = currency,
            onDismiss = { showAddJobDialog = false },
            onSave = { job ->
                viewModel.addServiceJob(job) {
                    showAddJobDialog = false
                }
            }
        )
    }

    if (showRecordPaymentDialog) {
        RecordPaymentDialog(
            customers = listOf(currentCustomer),
            preselectedCustomerId = currentCustomer.id,
            suggestedAmount = if (totalPending > 0) totalPending else null,
            currencySymbol = currency,
            onDismiss = { showRecordPaymentDialog = false },
            onSave = { pay ->
                viewModel.recordPayment(pay) {
                    showRecordPaymentDialog = false
                }
            }
        )
    }

    if (previewJob != null) {
        InvoicePreviewDialog(
            job = previewJob!!,
            customer = currentCustomer,
            businessProfile = profile,
            onDismiss = { previewJob = null }
        )
    }
}
