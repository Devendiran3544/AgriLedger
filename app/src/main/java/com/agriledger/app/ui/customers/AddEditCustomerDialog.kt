package com.agriledger.app.ui.customers

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
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.agriledger.app.ui.components.AgriLedgerFormField
import com.agriledger.app.ui.components.AgriLedgerPrimaryButton
import com.agriledger.app.ui.theme.PrimaryForest

@Composable
fun AddEditCustomerDialog(
    customerToEdit: Customer? = null,
    onDismiss: () -> Unit,
    onSave: (Customer) -> Unit
) {
    var name by remember { mutableStateOf(customerToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(customerToEdit?.phone ?: "") }
    var village by remember { mutableStateOf(customerToEdit?.village ?: "") }
    var acresText by remember {
        mutableStateOf(if (customerToEdit != null && customerToEdit.totalLandAcres > 0) customerToEdit.totalLandAcres.toString() else "")
    }
    var notes by remember { mutableStateOf(customerToEdit?.notes ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

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
                    text = if (customerToEdit == null) "Add New Customer" else "Edit Customer",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryForest
                    )
                )

                Text(
                    text = "Customer profile for billing & service records",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Name Field
                AgriLedgerFormField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = "Full Name *",
                    placeholder = "e.g. Harpreet Singh Sandhu",
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryForest)
                    },
                    isError = nameError,
                    errorMessage = "Name is required"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Phone Field
                AgriLedgerFormField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = false
                    },
                    label = "Mobile Number *",
                    placeholder = "e.g. 9876543210",
                    leadingIcon = {
                        Icon(Icons.Default.Call, contentDescription = null, tint = PrimaryForest)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneError,
                    errorMessage = "Phone number is required"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Village / Location
                AgriLedgerFormField(
                    value = village,
                    onValueChange = { village = it },
                    label = "Village / Location",
                    placeholder = "e.g. Kotli Kalan",
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryForest)
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Land Area (Acres)
                AgriLedgerFormField(
                    value = acresText,
                    onValueChange = { input ->
                        val sanitized = input.replace(',', '.')
                        if (sanitized.isEmpty() || sanitized.matches(Regex("^\\d*\\.?\\d*$"))) {
                            acresText = sanitized
                        }
                    },
                    label = "Total Farm Area (Optional)",
                    placeholder = "e.g. 15.5",
                    suffix = { Text("Acres") },
                    leadingIcon = {
                        Icon(Icons.Default.SquareFoot, contentDescription = null, tint = PrimaryForest)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Notes Field
                AgriLedgerFormField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes / Preferences",
                    placeholder = "e.g. Prefers UPI, wheat grower",
                    leadingIcon = {
                        Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = PrimaryForest)
                    },
                    singleLine = false,
                    maxLines = 3
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
                        text = if (customerToEdit == null) "Save Customer" else "Update",
                        onClick = {
                            var hasError = false
                            if (name.isBlank()) {
                                nameError = true
                                hasError = true
                            }
                            if (phone.isBlank()) {
                                phoneError = true
                                hasError = true
                            }

                            if (!hasError) {
                                val acres = acresText.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
                                val customer = (customerToEdit ?: Customer(name = "", phone = "")).copy(
                                    name = name.trim(),
                                    phone = phone.trim(),
                                    village = village.trim(),
                                    totalLandAcres = acres,
                                    notes = notes.trim()
                                )
                                onSave(customer)
                            }
                        },
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }
    }
}
