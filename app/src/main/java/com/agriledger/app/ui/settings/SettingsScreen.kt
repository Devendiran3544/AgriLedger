package com.agriledger.app.ui.settings

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.ui.components.AgriLedgerCard
import com.agriledger.app.ui.components.AgriLedgerFormField
import com.agriledger.app.ui.components.AgriLedgerPrimaryButton
import com.agriledger.app.ui.components.AgriLedgerTopBar
import com.agriledger.app.ui.theme.AccentGreen
import com.agriledger.app.ui.theme.AccentGreenLight
import com.agriledger.app.ui.theme.ErrorCrimson
import com.agriledger.app.ui.theme.ErrorCrimsonBg
import com.agriledger.app.ui.theme.PrimaryForest

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val profile = viewModel.businessProfile.collectAsState().value ?: BusinessProfile()
    val context = LocalContext.current

    var businessName by remember(profile) { mutableStateOf(profile.businessName) }
    var ownerName by remember(profile) { mutableStateOf(profile.ownerName) }
    var phone by remember(profile) { mutableStateOf(profile.phone) }
    var villageOrCity by remember(profile) { mutableStateOf(profile.villageOrCity) }
    var state by remember(profile) { mutableStateOf(profile.state) }
    var currencySymbol by remember(profile) { mutableStateOf(profile.currencySymbol) }
    var invoicePrefix by remember(profile) { mutableStateOf(profile.invoicePrefix) }

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AgriLedgerTopBar(
                title = "Settings",
                subtitle = "Business profile, invoice headers & data"
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Branding Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryForest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "AgriLedger",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Smart Agricultural Service Accounting",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                            Text(
                                text = "Version 1.0.0 • Offline-First Production Build",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AccentGreen,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Business Profile Form
            item {
                AgriLedgerCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Text(
                        text = "Business / Service Profile",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Printed on invoices, customer statements and share messages",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AgriLedgerFormField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = "Business / Farm Name",
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = PrimaryForest) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AgriLedgerFormField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = "Owner / Operator Name",
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryForest) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AgriLedgerFormField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Contact Phone",
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryForest) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AgriLedgerFormField(
                            value = villageOrCity,
                            onValueChange = { villageOrCity = it },
                            label = "Hub / Village",
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryForest) },
                            modifier = Modifier.weight(1f)
                        )

                        AgriLedgerFormField(
                            value = state,
                            onValueChange = { state = it },
                            label = "State",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AgriLedgerFormField(
                            value = currencySymbol,
                            onValueChange = { currencySymbol = it },
                            label = "Currency Symbol",
                            placeholder = "₹ or $",
                            leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = PrimaryForest) },
                            modifier = Modifier.weight(1f)
                        )

                        AgriLedgerFormField(
                            value = invoicePrefix,
                            onValueChange = { invoicePrefix = it },
                            label = "Invoice Prefix",
                            placeholder = "AL-",
                            leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = PrimaryForest) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    AgriLedgerPrimaryButton(
                        text = "Save Profile",
                        onClick = {
                            val updated = profile.copy(
                                businessName = businessName.trim().ifBlank { "AgriLedger Agro Services" },
                                ownerName = ownerName.trim().ifBlank { "Farm Owner" },
                                phone = phone.trim(),
                                villageOrCity = villageOrCity.trim(),
                                state = state.trim(),
                                currencySymbol = currencySymbol.trim().ifBlank { "₹" },
                                invoicePrefix = invoicePrefix.trim().ifBlank { "AL-" }
                            )
                            viewModel.saveBusinessProfile(updated) {
                                Toast.makeText(context, "Business Profile Updated", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // Quick Data Management (Demo Data Seeder & Database Cleaner)
            item {
                AgriLedgerCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp
                ) {
                    Text(
                        text = "Database Management",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Seed demo data for evaluation or reset local records",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1-Click Sample Data Seeder
                    Button(
                        onClick = {
                            viewModel.seedSampleData {
                                Toast.makeText(context, "Demo agricultural data loaded!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreenLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dataset,
                            contentDescription = null,
                            tint = PrimaryForest,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Load Realistic Demo Data",
                            color = PrimaryForest,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Reset Local Database
                    OutlinedButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorCrimson)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clear All Local Data",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear All Data?") },
            text = { Text("This will delete all saved customers, service jobs, invoices, payments, and expenses from your device. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData {
                            showClearConfirmDialog = false
                            Toast.makeText(context, "All data wiped", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorCrimson)
                ) {
                    Text("Delete Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
