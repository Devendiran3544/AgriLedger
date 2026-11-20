package com.agriledger.app.ui.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.window.DialogProperties
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.PaymentMode
import com.agriledger.app.ui.components.AgriLedgerFormField
import com.agriledger.app.ui.components.AgriLedgerPrimaryButton
import com.agriledger.app.ui.theme.ErrorCrimson
import com.agriledger.app.ui.theme.PrimaryForest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentDialog(
    customers: List<Customer>,
    preselectedCustomerId: Long? = null,
    suggestedAmount: Double? = null,
    currencySymbol: String = "₹",
    onDismiss: () -> Unit,
    onSave: (Payment) -> Unit
) {
    var selectedCustomerId by remember {
        mutableStateOf(preselectedCustomerId ?: customers.firstOrNull()?.id ?: 0L)
    }
    var customerDropdownExpanded by remember { mutableStateOf(false) }

    var selectedPaymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var modeDropdownExpanded by remember { mutableStateOf(false) }

    var amountText by remember {
        mutableStateOf(suggestedAmount?.let { if (it > 0) it.toInt().toString() else "" } ?: "")
    }
    var referenceNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var customerError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var amountErrorMessage by remember { mutableStateOf("Valid amount is required") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
                .imePadding(),
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
                    text = "Record Customer Payment",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryForest
                    )
                )

                Text(
                    text = "Log collection & reduce outstanding customer balance",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Customer Selection Dropdown
                Text(
                    text = "Customer / Farmer *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = customerDropdownExpanded,
                    onExpandedChange = { customerDropdownExpanded = !customerDropdownExpanded }
                ) {
                    val selectedCust = customers.find { it.id == selectedCustomerId }
                    OutlinedTextField(
                        value = selectedCust?.let { "${it.name} (${it.village})" } ?: "Select Customer",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerDropdownExpanded) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryForest) },
                        shape = RoundedCornerShape(14.dp),
                        isError = customerError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = customerDropdownExpanded,
                        onDismissRequest = { customerDropdownExpanded = false }
                    ) {
                        customers.forEach { cust ->
                            DropdownMenuItem(
                                text = { Text("${cust.name} - ${cust.village}") },
                                onClick = {
                                    selectedCustomerId = cust.id
                                    customerDropdownExpanded = false
                                    customerError = false
                                }
                            )
                        }
                    }
                }
                if (customerError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Please select a customer",
                        style = MaterialTheme.typography.labelSmall.copy(color = ErrorCrimson)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Field
                AgriLedgerFormField(
                    value = amountText,
                    onValueChange = { input ->
                        val sanitized = input.replace(',', '.')
                        if (sanitized.isEmpty() || sanitized.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amountText = sanitized
                            amountError = false
                        }
                    },
                    label = "Amount Received *",
                    placeholder = "e.g. 5000",
                    prefix = { Text(currencySymbol) },
                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, tint = PrimaryForest) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amountError,
                    errorMessage = amountErrorMessage
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Mode Dropdown
                Text(
                    text = "Payment Mode",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = modeDropdownExpanded,
                    onExpandedChange = { modeDropdownExpanded = !modeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPaymentMode.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeDropdownExpanded) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = modeDropdownExpanded,
                        onDismissRequest = { modeDropdownExpanded = false }
                    ) {
                        PaymentMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.displayName) },
                                onClick = {
                                    selectedPaymentMode = mode
                                    modeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reference / UTR Number
                AgriLedgerFormField(
                    value = referenceNumber,
                    onValueChange = { referenceNumber = it },
                    label = "Ref / UTR / Check No. (Optional)",
                    placeholder = "e.g. UPI-921893128"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Notes Field
                AgriLedgerFormField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes",
                    placeholder = "e.g. Paid in cash at mandi",
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
                        text = "Save Payment",
                        onClick = {
                            var hasError = false
                            val cleanAmount = amountText.trim().replace(',', '.').toDoubleOrNull()
                            if (selectedCustomerId <= 0L) {
                                customerError = true
                                hasError = true
                            }
                            if (amountText.isBlank()) {
                                amountError = true
                                amountErrorMessage = "Amount is required"
                                hasError = true
                            } else if (cleanAmount == null || cleanAmount <= 0.0) {
                                amountError = true
                                amountErrorMessage = "Amount must be greater than 0"
                                hasError = true
                            }

                            if (!hasError && cleanAmount != null) {
                                val payment = Payment(
                                    customerId = selectedCustomerId,
                                    amount = cleanAmount,
                                    paymentMode = selectedPaymentMode,
                                    referenceNumber = referenceNumber.trim(),
                                    notes = notes.trim()
                                )
                                onSave(payment)
                            }
                        },
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }
    }
}
