package com.agriledger.app.utils

import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.CustomerWithSummary
import com.agriledger.app.data.model.FinancialReport
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.ServiceJob
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    private val numberFormat = DecimalFormat("#,##,##0.00")
    private val integerNumberFormat = DecimalFormat("#,##,##0")
    private val decimalFormatOne = DecimalFormat("#,##0.0")

    fun formatCurrency(amount: Double, symbol: String = "₹"): String {
        return if (amount % 1.0 == 0.0) {
            "$symbol${integerNumberFormat.format(amount)}"
        } else {
            "$symbol${numberFormat.format(amount)}"
        }
    }

    fun formatAcres(acres: Double): String {
        return if (acres % 1.0 == 0.0) {
            "${acres.toInt()} Ac"
        } else {
            "${decimalFormatOne.format(acres)} Ac"
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun generateInvoiceShareText(
        job: ServiceJob,
        customer: Customer,
        businessName: String,
        currencySymbol: String
    ): String {
        return """
            ==============================
            🌾 $businessName
            SERVICE INVOICE
            ==============================
            Invoice No: ${job.invoiceNumber}
            Date: ${formatDate(job.serviceDate)}
            
            Customer: ${customer.name}
            Village: ${customer.village}
            Phone: ${customer.phone}
            ------------------------------
            Service: ${job.serviceType.displayName}
            Area: ${formatAcres(job.acres)}
            Rate: ${formatCurrency(job.ratePerAcre, currencySymbol)} / Acre
            
            Total Amount: ${formatCurrency(job.totalAmount, currencySymbol)}
            Amount Paid:  ${formatCurrency(job.amountPaid, currencySymbol)}
            Balance Due:  ${formatCurrency(job.pendingAmount, currencySymbol)}
            Status:       ${job.status.displayName.uppercase()}
            ------------------------------
            Notes: ${job.notes.ifBlank { "Thank you for your business!" }}
            ==============================
            Generated via AgriLedger
        """.trimIndent()
    }

    fun generateCustomerStatementText(
        summary: CustomerWithSummary,
        jobs: List<ServiceJob>,
        payments: List<Payment>,
        businessName: String,
        currencySymbol: String
    ): String {
        val sb = StringBuilder()
        sb.appendLine("==============================")
        sb.appendLine("🌾 $businessName")
        sb.appendLine("CUSTOMER ACCOUNT STATEMENT")
        sb.appendLine("==============================")
        sb.appendLine("Customer: ${summary.customer.name}")
        sb.appendLine("Phone: ${summary.customer.phone}")
        sb.appendLine("Village: ${summary.customer.village}")
        sb.appendLine("Total Area Serviced: ${formatAcres(summary.totalAcresServiced)}")
        sb.appendLine("------------------------------")
        sb.appendLine("Total Billed:    ${formatCurrency(summary.totalBilled, currencySymbol)}")
        sb.appendLine("Total Received:  ${formatCurrency(summary.totalPaid, currencySymbol)}")
        sb.appendLine("OUTSTANDING DUE: ${formatCurrency(summary.totalPending, currencySymbol)}")
        sb.appendLine("------------------------------")
        sb.appendLine("SERVICES RECORD:")
        if (jobs.isEmpty()) {
            sb.appendLine("No service jobs recorded.")
        } else {
            jobs.take(10).forEach { job ->
                sb.appendLine("• ${formatDate(job.serviceDate)} - ${job.serviceType.displayName}: ${formatAcres(job.acres)} = ${formatCurrency(job.totalAmount, currencySymbol)} [${job.status.displayName}]")
            }
        }
        sb.appendLine("------------------------------")
        sb.appendLine("PAYMENTS RECEIVED:")
        if (payments.isEmpty()) {
            sb.appendLine("No payments recorded.")
        } else {
            payments.take(10).forEach { pay ->
                sb.appendLine("• ${formatDate(pay.paymentDate)} - ${formatCurrency(pay.amount, currencySymbol)} via ${pay.paymentMode.displayName}")
            }
        }
        sb.appendLine("==============================")
        sb.appendLine("Generated via AgriLedger")
        return sb.toString()
    }

    fun generateFinancialReportText(
        report: FinancialReport,
        businessName: String,
        currencySymbol: String
    ): String {
        val sb = StringBuilder()
        sb.appendLine("==============================")
        sb.appendLine("🌾 $businessName")
        sb.appendLine("FINANCIAL SUMMARY REPORT")
        sb.appendLine("Date: ${formatDate(System.currentTimeMillis())}")
        sb.appendLine("==============================")
        sb.appendLine("Total Billed:    ${formatCurrency(report.totalBilled, currencySymbol)}")
        sb.appendLine("Total Collected: ${formatCurrency(report.totalCollected, currencySymbol)}")
        sb.appendLine("Total Pending:   ${formatCurrency(report.totalPending, currencySymbol)}")
        sb.appendLine("Total Expenses:  ${formatCurrency(report.totalExpenses, currencySymbol)}")
        sb.appendLine("------------------------------")
        sb.appendLine("NET CASH BALANCE: ${formatCurrency(report.netCashFlow, currencySymbol)}")
        sb.appendLine("Total Acres Serviced: ${formatAcres(report.totalAcresServiced)}")
        sb.appendLine("------------------------------")
        sb.appendLine("EXPENSES BY CATEGORY:")
        report.topExpenseCategories.forEach { exp ->
            sb.appendLine("• ${exp.category.displayName}: ${formatCurrency(exp.totalAmount, currencySymbol)} (${String.format(Locale.US, "%.1f", exp.percentage)}%)")
        }
        sb.appendLine("==============================")
        sb.appendLine("Generated via AgriLedger")
        return sb.toString()
    }
}
