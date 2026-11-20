package com.agriledger.app.ui.dashboard

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.OutstandingCustomer
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.ui.components.AgriLedgerCard
import com.agriledger.app.ui.components.HeroNetCashCard
import com.agriledger.app.ui.components.StatGridCard
import com.agriledger.app.ui.components.StatusBadge
import com.agriledger.app.ui.customers.AddEditCustomerDialog
import com.agriledger.app.ui.expenses.AddExpenseDialog
import com.agriledger.app.ui.payments.RecordPaymentDialog
import com.agriledger.app.ui.services.AddServiceJobDialog
import com.agriledger.app.ui.theme.AccentGreen
import com.agriledger.app.ui.theme.AccentGreenLight
import com.agriledger.app.ui.theme.ErrorCrimson
import com.agriledger.app.ui.theme.ErrorCrimsonBg
import com.agriledger.app.ui.theme.InfoBlue
import com.agriledger.app.ui.theme.InfoBlueBg
import com.agriledger.app.ui.theme.PrimaryForest
import com.agriledger.app.ui.theme.PrimaryForestVariant
import com.agriledger.app.ui.theme.SuccessGreen
import com.agriledger.app.ui.theme.SuccessGreenBg
import com.agriledger.app.ui.theme.WarningGold
import com.agriledger.app.ui.theme.WarningGoldBg
import com.agriledger.app.utils.Formatters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToCustomers: () -> Unit,
    onNavigateToCustomerDetail: (Long) -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val recentJobs by viewModel.recentJobs.collectAsState()
    val recentPayments by viewModel.recentPayments.collectAsState()
    val recentExpenses by viewModel.recentExpenses.collectAsState()
    val outstandingCustomers by viewModel.outstandingCustomers.collectAsState()
    val profile = viewModel.businessProfile.collectAsState().value ?: BusinessProfile()
    val allCustomers by viewModel.allCustomers.collectAsState()

    val currency = profile.currencySymbol
    val context = LocalContext.current

    // Quick Action Dialog States
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var showAddJobDialog by remember { mutableStateOf(false) }
    var showRecordPaymentDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    // Quick Action preselected customer
    var paymentPreselectedCustomer by remember { mutableStateOf<Customer?>(null) }

    // Recent activity tab: 0 = Invoices, 1 = Payments, 2 = Expenses
    var selectedActivityTab by remember { mutableIntStateOf(0) }

    val currentDateStr = remember {
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
    }

    val greeting = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..11 -> "Good Morning ☀️"
            in 12..16 -> "Good Afternoon 🌤️"
            in 17..20 -> "Good Evening 🌅"
            else -> "Good Night 🌙"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AccentGreen.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = "AgriLedger",
                                tint = AccentGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AgriLedger",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = profile.businessName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Offline-Ready",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryForest
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header Greeting & Date
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryForest)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = AccentGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = currentDateStr,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Hero Net Cash Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryForest)
                        .padding(horizontal = 20.dp)
                ) {
                    HeroNetCashCard(
                        netCash = Formatters.formatCurrency(stats.netCashBalance, currency),
                        totalCollected = Formatters.formatCurrency(stats.totalCollected, currency),
                        totalExpenses = Formatters.formatCurrency(stats.totalExpenses, currency),
                        currencySymbol = currency
                    )
                }
            }

            // Quick Actions Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.PersonAdd,
                            label = "Add Customer",
                            color = PrimaryForest,
                            bgColor = AccentGreenLight,
                            onClick = { showAddCustomerDialog = true },
                            modifier = Modifier.weight(1f)
                        )

                        QuickActionButton(
                            icon = Icons.Default.Agriculture,
                            label = "New Service",
                            color = InfoBlue,
                            bgColor = InfoBlueBg,
                            onClick = { showAddJobDialog = true },
                            modifier = Modifier.weight(1f)
                        )

                        QuickActionButton(
                            icon = Icons.Default.Payments,
                            label = "Record Pay",
                            color = SuccessGreen,
                            bgColor = SuccessGreenBg,
                            onClick = {
                                paymentPreselectedCustomer = null
                                showRecordPaymentDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        )

                        QuickActionButton(
                            icon = Icons.Default.ReceiptLong,
                            label = "Add Expense",
                            color = ErrorCrimson,
                            bgColor = ErrorCrimsonBg,
                            onClick = { showAddExpenseDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Financial Summary Grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Financial Overview",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )

                        TextButton(onClick = onNavigateToReports) {
                            Text(
                                text = "View Reports",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = PrimaryForest,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 1: Total Billed & Total Pending
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatGridCard(
                            title = "Total Billed",
                            value = Formatters.formatCurrency(stats.totalBilled, currency),
                            subtitle = "All invoices",
                            icon = Icons.Default.Receipt,
                            iconTint = InfoBlue,
                            iconBgColor = InfoBlueBg,
                            modifier = Modifier.weight(1f)
                        )

                        StatGridCard(
                            title = "Total Pending",
                            value = Formatters.formatCurrency(stats.totalPending, currency),
                            subtitle = "Receivables due",
                            icon = Icons.Default.PendingActions,
                            iconTint = if (stats.totalPending > 0) ErrorCrimson else SuccessGreen,
                            iconBgColor = if (stats.totalPending > 0) ErrorCrimsonBg else SuccessGreenBg,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row 2: Total Customers & Acres Serviced
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatGridCard(
                            title = "Customers",
                            value = "${stats.totalCustomers}",
                            subtitle = "Total registered",
                            icon = Icons.Default.Group,
                            iconTint = PrimaryForest,
                            iconBgColor = AccentGreenLight,
                            onClick = onNavigateToCustomers,
                            modifier = Modifier.weight(1f)
                        )

                        StatGridCard(
                            title = "Acres Serviced",
                            value = Formatters.formatAcres(stats.totalAcresServiced),
                            subtitle = "Work completed",
                            icon = Icons.Default.Landscape,
                            iconTint = AccentGreen,
                            iconBgColor = AccentGreenLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Outstanding Payments Section (if any customer has balance due)
            if (outstandingCustomers.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Outstanding Balances",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(ErrorCrimsonBg)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${outstandingCustomers.size}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ErrorCrimson,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            TextButton(onClick = onNavigateToCustomers) {
                                Text(
                                    text = "See All",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = PrimaryForest,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(outstandingCustomers.take(6)) { cust ->
                                OutstandingCustomerCard(
                                    customer = cust,
                                    currencySymbol = currency,
                                    onCall = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${cust.phone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    onRecordPayment = {
                                        val matchedCustomer = allCustomers.find { it.id == cust.customerId }
                                        paymentPreselectedCustomer = matchedCustomer
                                        showRecordPaymentDialog = true
                                    },
                                    onClick = { onNavigateToCustomerDetail(cust.customerId) }
                                )
                            }
                        }
                    }
                }
            }

            // Recent Activity Tabs
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp)
                ) {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedActivityTab,
                        edgePadding = 20.dp,
                        containerColor = Color.Transparent,
                        contentColor = PrimaryForest,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedActivityTab]),
                                color = PrimaryForest
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedActivityTab == 0,
                            onClick = { selectedActivityTab = 0 },
                            text = { Text("Recent Invoices (${recentJobs.size})") }
                        )
                        Tab(
                            selected = selectedActivityTab == 1,
                            onClick = { selectedActivityTab = 1 },
                            text = { Text("Payments (${recentPayments.size})") }
                        )
                        Tab(
                            selected = selectedActivityTab == 2,
                            onClick = { selectedActivityTab = 2 },
                            text = { Text("Expenses (${recentExpenses.size})") }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (selectedActivityTab) {
                        0 -> {
                            // Recent Invoices List
                            if (recentJobs.isEmpty()) {
                                Text(
                                    text = "No service jobs yet. Tap 'New Service' to create one.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(20.dp)
                                )
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    recentJobs.take(5).forEach { job ->
                                        val cust = allCustomers.find { it.id == job.customerId }
                                        RecentJobItem(
                                            job = job,
                                            customerName = cust?.name ?: "Customer #${job.customerId}",
                                            currencySymbol = currency,
                                            onClick = { onNavigateToCustomerDetail(job.customerId) }
                                        )
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Recent Payments List
                            if (recentPayments.isEmpty()) {
                                Text(
                                    text = "No payments received yet. Tap 'Record Pay' to add one.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(20.dp)
                                )
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    recentPayments.take(5).forEach { pay ->
                                        val cust = allCustomers.find { it.id == pay.customerId }
                                        RecentPaymentItem(
                                            payment = pay,
                                            customerName = cust?.name ?: "Customer #${pay.customerId}",
                                            currencySymbol = currency
                                        )
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Recent Expenses List
                            if (recentExpenses.isEmpty()) {
                                Text(
                                    text = "No expenses logged yet. Tap 'Add Expense' to track costs.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(20.dp)
                                )
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    recentExpenses.take(5).forEach { exp ->
                                        RecentExpenseItem(
                                            expense = exp,
                                            currencySymbol = currency
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
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

    if (showAddJobDialog) {
        AddServiceJobDialog(
            customers = allCustomers,
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
            customers = allCustomers,
            preselectedCustomerId = paymentPreselectedCustomer?.id,
            currencySymbol = currency,
            onDismiss = { showRecordPaymentDialog = false },
            onSave = { payment ->
                viewModel.recordPayment(payment) {
                    showRecordPaymentDialog = false
                }
            }
        )
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            currencySymbol = currency,
            onDismiss = { showAddExpenseDialog = false },
            onSave = { expense ->
                viewModel.addExpense(expense) {
                    showAddExpenseDialog = false
                }
            }
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun OutstandingCustomerCard(
    customer: OutstandingCustomer,
    currencySymbol: String,
    onCall: () -> Unit,
    onRecordPayment: () -> Unit,
    onClick: () -> Unit
) {
    AgriLedgerCard(
        modifier = Modifier.width(230.dp),
        cornerRadius = 18.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.customerName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Text(
                    text = customer.village.ifBlank { "Farmer" },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1
                )
            }

            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AccentGreenLight)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call",
                    tint = PrimaryForest,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "Balance Due",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = Formatters.formatCurrency(customer.pendingAmount, currencySymbol),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ErrorCrimson
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryForest)
                    .clickable(onClick = onRecordPayment)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Collect",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun RecentJobItem(
    job: ServiceJob,
    customerName: String,
    currencySymbol: String,
    onClick: () -> Unit
) {
    AgriLedgerCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customerName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${job.serviceType.displayName} • ${Formatters.formatAcres(job.acres)}",
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
                    text = Formatters.formatCurrency(job.totalAmount, currencySymbol),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = job.status)
            }
        }
    }
}

@Composable
private fun RecentPaymentItem(
    payment: Payment,
    customerName: String,
    currencySymbol: String
) {
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
                    text = customerName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "via ${payment.paymentMode.displayName}${if (payment.referenceNumber.isNotBlank()) " (${payment.referenceNumber})" else ""}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = Formatters.formatDate(payment.paymentDate),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
            }

            Text(
                text = "+${Formatters.formatCurrency(payment.amount, currencySymbol)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
            )
        }
    }
}

@Composable
private fun RecentExpenseItem(
    expense: Expense,
    currencySymbol: String
) {
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
                    text = expense.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = expense.category.displayName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = Formatters.formatDate(expense.expenseDate),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
            }

            Text(
                text = "-${Formatters.formatCurrency(expense.amount, currencySymbol)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ErrorCrimson
                )
            )
        }
    }
}
