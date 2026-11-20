package com.agriledger.app.ui.expenses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.ExpenseCategory
import com.agriledger.app.ui.components.AgriLedgerCard
import com.agriledger.app.ui.components.AgriLedgerFormField
import com.agriledger.app.ui.theme.AccentGreen
import com.agriledger.app.ui.theme.AccentGreenLight
import com.agriledger.app.ui.theme.CashCardGradient
import com.agriledger.app.ui.theme.ErrorCrimson
import com.agriledger.app.ui.theme.ErrorCrimsonBg
import com.agriledger.app.ui.theme.PrimaryForest
import com.agriledger.app.ui.theme.WarningGold
import com.agriledger.app.ui.theme.WarningGoldBg
import com.agriledger.app.utils.Formatters

data class CalculatorLineItem(
    val id: Long = System.currentTimeMillis() + (0..10000).random(),
    val name: String,
    val amount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCalculatorView(
    currencySymbol: String = "₹",
    onSaveExpense: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember { mutableStateListOf<CalculatorLineItem>() }

    var itemName by remember { mutableStateOf("") }
    var itemAmountText by remember { mutableStateOf("") }
    var editingItemId by remember { mutableStateOf<Long?>(null) }

    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    // Dialogs
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }

    // Save as expense dialog state
    var expenseTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.MAINTENANCE) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val totalCalculation = items.sumOf { it.amount }

    Column(modifier = modifier.fillMaxWidth()) {
        // Hero Total Display Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CashCardGradient)
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AccentGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CALCULATOR TOTAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        if (items.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WarningGoldBg)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Unsaved (${items.size} items)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = WarningGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = Formatters.formatCurrency(totalCalculation, currencySymbol),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Text(
                        text = "Sum of ${items.size} expense items • Ready to log into ledger",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Input Form to Add / Edit Line Item
        AgriLedgerCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            cornerRadius = 18.dp
        ) {
            Text(
                text = if (editingItemId == null) "Add Expense Item" else "Edit Expense Item",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AgriLedgerFormField(
                    value = itemName,
                    onValueChange = {
                        itemName = it
                        nameError = false
                    },
                    label = "Item Name *",
                    placeholder = "e.g. 50L Diesel",
                    isError = nameError,
                    errorMessage = "Required",
                    modifier = Modifier.weight(1.3f)
                )

                AgriLedgerFormField(
                    value = itemAmountText,
                    onValueChange = {
                        itemAmountText = it
                        amountError = false
                    },
                    label = "Amount *",
                    placeholder = "4500",
                    prefix = { Text(currencySymbol) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    errorMessage = "Required",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (editingItemId != null) {
                    TextButton(
                        onClick = {
                            editingItemId = null
                            itemName = ""
                            itemAmountText = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        var hasError = false
                        val amount = itemAmountText.toDoubleOrNull() ?: 0.0
                        if (itemName.isBlank()) {
                            nameError = true
                            hasError = true
                        }
                        if (amount <= 0.0) {
                            amountError = true
                            hasError = true
                        }

                        if (!hasError) {
                            if (editingItemId != null) {
                                val idx = items.indexOfFirst { it.id == editingItemId }
                                if (idx != -1) {
                                    items[idx] = CalculatorLineItem(
                                        id = editingItemId!!,
                                        name = itemName.trim(),
                                        amount = amount
                                    )
                                }
                                editingItemId = null
                            } else {
                                items.add(
                                    CalculatorLineItem(
                                        name = itemName.trim(),
                                        amount = amount
                                    )
                                )
                            }
                            itemName = ""
                            itemAmountText = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Icon(
                        imageVector = if (editingItemId == null) Icons.Default.Add else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (editingItemId == null) "Add Item" else "Update Item",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Action Toolbar (Save as Expense & Clear with Unsaved Protection)
        if (items.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        expenseTitle = items.joinToString(", ") { it.name }.take(50)
                        showSaveDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save As Expense", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showClearConfirmDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorCrimson)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear")
                }
            }
        }

        // Line Items List
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Calculator is empty",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add line items above to calculate multi-item maintenance or fuel jobs.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(AccentGreenLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = PrimaryForest,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = Formatters.formatCurrency(item.amount, currencySymbol),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryForest
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = {
                                        editingItemId = item.id
                                        itemName = item.name
                                        itemAmountText = if (item.amount % 1.0 == 0.0) item.amount.toInt().toString() else item.amount.toString()
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = PrimaryForest,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { items.remove(item) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = ErrorCrimson,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Unsaved-State Clear Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Discard Calculator Items?") },
            text = {
                Text("You have ${items.size} unsaved calculation line items totaling ${Formatters.formatCurrency(totalCalculation, currencySymbol)}. Are you sure you want to clear them?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        items.clear()
                        editingItemId = null
                        itemName = ""
                        itemAmountText = ""
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson)
                ) {
                    Text("Clear All Items")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Keep Items")
                }
            }
        )
    }

    // Save As Expense Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Calculation as Expense") },
            text = {
                Column {
                    Text(
                        text = "Total calculated amount: ${Formatters.formatCurrency(totalCalculation, currencySymbol)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryForest
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AgriLedgerFormField(
                        value = expenseTitle,
                        onValueChange = { expenseTitle = it },
                        label = "Expense Title *"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Category *",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.displayName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            ExpenseCategory.entries.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (expenseTitle.isNotBlank()) {
                            val breakdownNotes = items.mapIndexed { idx, it ->
                                "${idx + 1}. ${it.name}: ${Formatters.formatCurrency(it.amount, currencySymbol)}"
                            }.joinToString("\n")

                            val expense = Expense(
                                category = selectedCategory,
                                amount = totalCalculation,
                                title = expenseTitle.trim(),
                                notes = "Multi-Item Calculator Breakdown:\n$breakdownNotes"
                            )
                            onSaveExpense(expense)
                            items.clear()
                            showSaveDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Text("Save to Ledger")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
