package com.agriledger.app.ui.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SquareFoot
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.data.model.ServiceType
import com.agriledger.app.ui.components.AgriLedgerFormField
import com.agriledger.app.ui.components.AgriLedgerPrimaryButton
import com.agriledger.app.ui.theme.AccentGreenLight
import com.agriledger.app.ui.theme.PrimaryForest
import com.agriledger.app.utils.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceJobDialog(
    customers: List<Customer>,
    preselectedCustomerId: Long? = null,
    currencySymbol: String = "₹",
    onDismiss: () -> Unit,
    onSave: (ServiceJob) -> Unit
) {
    var selectedCustomerId by remember {
        mutableStateOf(preselectedCustomerId ?: customers.firstOrNull()?.id ?: 0L)
    }
    var customerDropdownExpanded by remember { mutableStateOf(false) }

    var selectedServiceType by remember { mutableStateOf(ServiceType.HARVESTING) }
    var serviceDropdownExpanded by remember { mutableStateOf(false) }

    var acresText by remember { mutableStateOf("10") }
    var rateText by remember { mutableStateOf(selectedServiceType.defaultRatePerAcre.toInt().toString()) }
    var amountPaidText by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    val acres = acresText.toDoubleOrNull() ?: 0.0
    val rate = rateText.toDoubleOrNull() ?: 0.0
    val totalAmount = acres * rate
    val amountPaid = amountPaidText.toDoubleOrNull() ?: 0.0

    var customerError by remember { mutableStateOf(false) }
    var acresError by remember { mutableStateOf(false) }

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
                    text = "New Service Job / Bill",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryForest
                    )
                )

                Text(
                    text = "Record agricultural service delivered & bill customer",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Customer Dropdown
                Text(
                    text = "Customer *",
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
                    val currentCustomer = customers.find { it.id == selectedCustomerId }
                    OutlinedTextField(
                        value = currentCustomer?.let { "${it.name} (${it.village})" } ?: "Select Customer",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerDropdownExpanded) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryForest) },
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

                Spacer(modifier = Modifier.height(14.dp))

                // Service Type Dropdown
                Text(
                    text = "Service Type *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = serviceDropdownExpanded,
                    onExpandedChange = { serviceDropdownExpanded = !serviceDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedServiceType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceDropdownExpanded) },
                        leadingIcon = { Icon(Icons.Default.Agriculture, contentDescription = null, tint = PrimaryForest) },
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
                        expanded = serviceDropdownExpanded,
                        onDismissRequest = { serviceDropdownExpanded = false }
                    ) {
                        ServiceType.entries.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.displayName) },
                                onClick = {
                                    selectedServiceType = st
                                    rateText = st.defaultRatePerAcre.toInt().toString()
                                    serviceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Acres & Rate Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AgriLedgerFormField(
                        value = acresText,
                        onValueChange = {
                            acresText = it
                            acresError = false
                        },
                        label = "Area (Acres) *",
                        placeholder = "10",
                        leadingIcon = { Icon(Icons.Default.SquareFoot, contentDescription = null, tint = PrimaryForest) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = acresError,
                        errorMessage = "Required",
                        modifier = Modifier.weight(1f)
                    )

                    AgriLedgerFormField(
                        value = rateText,
                        onValueChange = { rateText = it },
                        label = "Rate / Acre *",
                        placeholder = "2500",
                        prefix = { Text(currencySymbol) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total Calculation Display Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AccentGreenLight)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CALCULATED TOTAL BILL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryForest
                                )
                            )
                            Text(
                                text = "$acres Acres × $currencySymbol$rate",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PrimaryForest.copy(alpha = 0.8f)
                                )
                            )
                        }

                        Text(
                            text = Formatters.formatCurrency(totalAmount, currencySymbol),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryForest
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Initial Amount Paid (Optional Advance)
                AgriLedgerFormField(
                    value = amountPaidText,
                    onValueChange = { amountPaidText = it },
                    label = "Advance / Initial Payment Received",
                    placeholder = "0",
                    prefix = { Text(currencySymbol) },
                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, tint = PrimaryForest) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Notes Field
                AgriLedgerFormField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes / Equipment Used",
                    placeholder = "e.g. John Deere 5310, 7ft Rotavator",
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
                        text = "Create Bill",
                        onClick = {
                            var hasError = false
                            if (selectedCustomerId <= 0L) {
                                customerError = true
                                hasError = true
                            }
                            if (acres <= 0.0) {
                                acresError = true
                                hasError = true
                            }

                            if (!hasError) {
                                val status = when {
                                    amountPaid >= totalAmount -> PaymentStatus.PAID
                                    amountPaid > 0 -> PaymentStatus.PARTIAL
                                    else -> PaymentStatus.UNPAID
                                }
                                val job = ServiceJob(
                                    customerId = selectedCustomerId,
                                    invoiceNumber = "", // auto-generated in repository
                                    serviceType = selectedServiceType,
                                    acres = acres,
                                    ratePerAcre = rate,
                                    totalAmount = totalAmount,
                                    amountPaid = amountPaid,
                                    status = status,
                                    notes = notes.trim()
                                )
                                onSave(job)
                            }
                        },
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }
    }
}
