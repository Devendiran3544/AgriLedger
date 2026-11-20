package com.agriledger.app.ui.reports

import android.content.Intent
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.ExpenseCategoryStat
import com.agriledger.app.data.model.FinancialReport
import com.agriledger.app.data.model.ServiceTypeStat
import com.agriledger.app.ui.components.AgriLedgerCard
import com.agriledger.app.ui.components.AgriLedgerOutlinedButton
import com.agriledger.app.ui.components.AgriLedgerTopBar
import com.agriledger.app.ui.components.EmptyStateView
import com.agriledger.app.ui.theme.AccentGreen
import com.agriledger.app.ui.theme.AccentGreenLight
import com.agriledger.app.ui.theme.CashCardGradient
import com.agriledger.app.ui.theme.ErrorCrimson
import com.agriledger.app.ui.theme.ErrorCrimsonBg
import com.agriledger.app.ui.theme.InfoBlue
import com.agriledger.app.ui.theme.InfoBlueBg
import com.agriledger.app.ui.theme.PrimaryForest
import com.agriledger.app.ui.theme.SuccessGreen
import com.agriledger.app.ui.theme.SuccessGreenBg
import com.agriledger.app.ui.theme.WarningGold
import com.agriledger.app.utils.Formatters
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel
) {
    val report by viewModel.financialReport.collectAsState()
    val profile = viewModel.businessProfile.collectAsState().value ?: BusinessProfile()
    val currency = profile.currencySymbol
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AgriLedgerTopBar(
                title = "Financial Reports",
                subtitle = "Income statement, expense breakdown & audit"
            )
        }
    ) { paddingValues ->
        if (report == null || (report!!.totalBilled == 0.0 && report!!.totalExpenses == 0.0)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    icon = Icons.Default.Analytics,
                    title = "No financial data yet",
                    description = "Add service jobs, collections, and expenses to generate comprehensive agricultural accounting reports."
                )
            }
        } else {
            val r = report!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Executive Net Cash Hero Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CashCardGradient)
                                .padding(20.dp)
                        ) {
                            Column {
                                Text(
                                    text = "NET REALIZED CASH FLOW",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = Formatters.formatCurrency(r.netCashFlow, currency),
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Actual Cash Inflow (${Formatters.formatCurrency(r.totalCollected, currency)}) - Outflow (${Formatters.formatCurrency(r.totalExpenses, currency)})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Income Statement Card
                item {
                    AgriLedgerCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp
                    ) {
                        Text(
                            text = "Income & Collections Summary",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        FinancialRow(
                            label = "Total Service Invoices Billed",
                            amount = Formatters.formatCurrency(r.totalBilled, currency),
                            color = InfoBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FinancialRow(
                            label = "Payments Received (Collected)",
                            amount = "+${Formatters.formatCurrency(r.totalCollected, currency)}",
                            color = SuccessGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FinancialRow(
                            label = "Pending Receivables (Due)",
                            amount = Formatters.formatCurrency(r.totalPending, currency),
                            color = if (r.totalPending > 0) ErrorCrimson else SuccessGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FinancialRow(
                            label = "Total Operating Expenses",
                            amount = "-${Formatters.formatCurrency(r.totalExpenses, currency)}",
                            color = ErrorCrimson
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        FinancialRow(
                            label = "Net Cash Balance",
                            amount = Formatters.formatCurrency(r.netCashFlow, currency),
                            color = PrimaryForest,
                            isBold = true
                        )
                    }
                }

                // Operational Stats Card
                item {
                    AgriLedgerCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp
                    ) {
                        Text(
                            text = "Operational Metrics",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Farmland Serviced:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = Formatters.formatAcres(r.totalAcresServiced),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        if (r.totalAcresServiced > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Avg Revenue / Acre:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = Formatters.formatCurrency(r.totalBilled / r.totalAcresServiced, currency),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                // Expense Categories Breakdown
                if (r.topExpenseCategories.isNotEmpty()) {
                    item {
                        AgriLedgerCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 20.dp
                        ) {
                            Text(
                                text = "Expenses by Category",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            r.topExpenseCategories.forEach { catStat ->
                                ExpenseCategoryRow(catStat = catStat, currencySymbol = currency)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }

                // Service Distribution Breakdown
                if (r.serviceBreakdowns.isNotEmpty()) {
                    item {
                        AgriLedgerCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 20.dp
                        ) {
                            Text(
                                text = "Revenue by Service Type",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            r.serviceBreakdowns.forEach { serviceStat ->
                                ServiceTypeRow(serviceStat = serviceStat, currencySymbol = currency)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }

                // Export / Share Report Button
                item {
                    AgriLedgerOutlinedButton(
                        text = "Share Financial Statement",
                        icon = Icons.Default.Share,
                        onClick = {
                            val text = Formatters.generateFinancialReportText(
                                report = r,
                                businessName = profile.businessName,
                                currencySymbol = currency
                            )
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Financial Report"))
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
private fun FinancialRow(
    label: String,
    amount: String,
    color: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyMedium
        )
        Text(
            text = amount,
            style = if (isBold) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = color)
            else MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = color)
        )
    }
}

@Composable
private fun ExpenseCategoryRow(
    catStat: ExpenseCategoryStat,
    currencySymbol: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = catStat.category.displayName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = "${Formatters.formatCurrency(catStat.totalAmount, currencySymbol)} (${String.format(Locale.US, "%.1f", catStat.percentage)}%)",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (catStat.percentage / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = ErrorCrimson,
            trackColor = ErrorCrimsonBg
        )
    }
}

@Composable
private fun ServiceTypeRow(
    serviceStat: ServiceTypeStat,
    currencySymbol: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = serviceStat.serviceType.displayName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "${Formatters.formatAcres(serviceStat.totalAcres)} serviced • ${serviceStat.jobCount} jobs",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Text(
            text = Formatters.formatCurrency(serviceStat.totalBilled, currencySymbol),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}
