package com.agriledger.app.ui.expenses

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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import com.agriledger.app.ui.theme.AccentGreenLight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.ExpenseCategory
import com.agriledger.app.ui.components.AgriLedgerCard
import com.agriledger.app.ui.components.AgriLedgerTopBar
import com.agriledger.app.ui.components.EmptyStateView
import com.agriledger.app.ui.components.SearchAndFilterBar
import com.agriledger.app.ui.theme.ErrorCrimson
import com.agriledger.app.ui.theme.ErrorCrimsonBg
import com.agriledger.app.ui.theme.PrimaryForest
import com.agriledger.app.utils.Formatters

@Composable
fun ExpensesScreen(
    viewModel: ExpenseViewModel
) {
    val expenses by viewModel.expenses.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val selectedFilter by viewModel.selectedCategoryFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val profile = viewModel.businessProfile.collectAsState().value ?: BusinessProfile()
    val currency = profile.currencySymbol

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var selectedScreenTab by remember { mutableIntStateOf(0) } // 0 = Expense History, 1 = Multi-Item Calculator

    val filterOptions = remember {
        listOf("All") + ExpenseCategory.entries.map { it.displayName }
    }

    Scaffold(
        topBar = {
            AgriLedgerTopBar(
                title = "Expense Management",
                subtitle = "Track diesel, maintenance & operational costs"
            )
        },
        floatingActionButton = {
            if (selectedScreenTab == 0) {
                FloatingActionButton(
                    onClick = { showAddExpenseDialog = true },
                    containerColor = ErrorCrimson,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mode Switcher Tabs
            TabRow(
                selectedTabIndex = selectedScreenTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryForest,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedScreenTab]),
                        color = PrimaryForest
                    )
                }
            ) {
                Tab(
                    selected = selectedScreenTab == 0,
                    onClick = { selectedScreenTab = 0 },
                    text = {
                        Text(
                            text = "Expense Ledger",
                            fontWeight = if (selectedScreenTab == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = selectedScreenTab == 1,
                    onClick = { selectedScreenTab = 1 },
                    text = {
                        Text(
                            text = "Multi-Item Calculator",
                            fontWeight = if (selectedScreenTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }

            if (selectedScreenTab == 1) {
                // Dedicated Multi-Item Expense Calculator
                ExpenseCalculatorView(
                    currencySymbol = currency,
                    onSaveExpense = { exp ->
                        viewModel.addExpense(exp) {
                            selectedScreenTab = 0
                        }
                    }
                )
            } else {
                // Total Filtered Expense Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(ErrorCrimsonBg)
                        .padding(18.dp)
                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedFilter == "All") "TOTAL OPERATING EXPENSES" else selectedFilter.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ErrorCrimson,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Formatters.formatCurrency(totalExpenses, currency),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ErrorCrimson
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ErrorCrimson.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = ErrorCrimson,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Search and Category Filter
            SearchAndFilterBar(
                searchQuery = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholderText = "Search expenses...",
                filterOptions = filterOptions,
                selectedFilter = selectedFilter,
                onFilterSelected = viewModel::onCategoryFilterChange,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            // Direct shortcut to Multi-Item Calculator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECORDED EXPENSES (${expenses.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                )
                TextButton(
                    onClick = { selectedScreenTab = 1 },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = PrimaryForest,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Open Calculator",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryForest
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (expenses.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ReceiptLong,
                    title = if (searchQuery.isNotEmpty()) "No matching expenses" else "No expenses logged",
                    description = if (searchQuery.isNotEmpty()) "Try adjusting your filter or search query" else "Log fuel, maintenance, driver wages and other costs to see accurate net cash flow.",
                    actionButtonText = if (searchQuery.isEmpty()) "Log First Expense" else null,
                    onActionClick = { showAddExpenseDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(expenses) { expense ->
                        ExpenseListItem(
                            expense = expense,
                            currencySymbol = currency,
                            onDelete = { viewModel.deleteExpense(expense) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            }
        }
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
private fun ExpenseListItem(
    expense: Expense,
    currencySymbol: String,
    onDelete: () -> Unit
) {
    AgriLedgerCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp
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
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ErrorCrimsonBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (expense.category == ExpenseCategory.FUEL) Icons.Default.LocalGasStation else Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = ErrorCrimson,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = expense.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = expense.category.displayName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PrimaryForest,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "${Formatters.formatDate(expense.expenseDate)}${if (expense.receiptNumber.isNotBlank()) " • Bill #${expense.receiptNumber}" else ""}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "-${Formatters.formatCurrency(expense.amount, currencySymbol)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ErrorCrimson
                    )
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (expense.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Note: ${expense.notes}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            )
        }
    }
}
