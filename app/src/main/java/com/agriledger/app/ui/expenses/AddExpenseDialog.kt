package com.agriledger.app.ui.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.ExpenseCategory
import com.agriledger.app.ui.components.AgriLedgerFormField
import com.agriledger.app.ui.components.AgriLedgerPrimaryButton
import com.agriledger.app.ui.theme.PrimaryForest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    currencySymbol: String = "₹",
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.FUEL) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var receiptNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Record Business Expense",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryForest
                    )
                )

                Text(
                    text = "Fuel, machinery maintenance, wages, and operating costs",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Expense Category Dropdown
                Text(
                    text = "Category *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, tint = PrimaryForest) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
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

                Spacer(modifier = Modifier.height(14.dp))

                // Title / Description Field
                AgriLedgerFormField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = "Expense Title / Description *",
                    placeholder = "e.g. 100L Diesel for Harvester",
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = PrimaryForest) },
                    isError = titleError,
                    errorMessage = "Title is required"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Field
                AgriLedgerFormField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        amountError = false
                    },
                    label = "Amount *",
                    placeholder = "e.g. 9500",
                    prefix = { Text(currencySymbol) },
                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, tint = PrimaryForest) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    errorMessage = "Valid amount is required"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Receipt / Bill Number
                AgriLedgerFormField(
                    value = receiptNumber,
                    onValueChange = { receiptNumber = it },
                    label = "Bill / Voucher / Receipt No. (Optional)",
                    placeholder = "e.g. IOC-88219"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Notes Field
                AgriLedgerFormField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes / Station Name",
                    placeholder = "e.g. Highway pump, full tank for machine #1",
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = PrimaryForest) },
                    singleLine = false,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    AgriLedgerPrimaryButton(
                        text = "Save Expense",
                        onClick = {
                            var hasError = false
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            if (title.isBlank()) {
                                titleError = true
                                hasError = true
                            }
                            if (amount <= 0.0) {
                                amountError = true
                                hasError = true
                            }

                            if (!hasError) {
                                val expense = Expense(
                                    category = selectedCategory,
                                    amount = amount,
                                    title = title.trim(),
                                    receiptNumber = receiptNumber.trim(),
                                    notes = notes.trim()
                                )
                                onSave(expense)
                            }
                        },
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }
    }
}
