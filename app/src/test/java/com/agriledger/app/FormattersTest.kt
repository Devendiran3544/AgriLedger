package com.agriledger.app

import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.data.model.ServiceType
import com.agriledger.app.utils.Formatters
import org.junit.Assert.assertTrue
import org.junit.Test

class FormattersTest {

    @Test
    fun testCurrencyFormatting() {
        val formatted = Formatters.formatCurrency(12500.0, "₹")
        assertTrue(formatted.contains("12,500"))
        assertTrue(formatted.startsWith("₹"))
    }

    @Test
    fun testAcresFormatting() {
        val wholeAcres = Formatters.formatAcres(15.0)
        assertTrue(wholeAcres.contains("15"))
        assertTrue(wholeAcres.endsWith("Ac"))

        val decimalAcres = Formatters.formatAcres(15.5)
        assertTrue(decimalAcres.contains("15.5"))
    }

    @Test
    fun testGenerateInvoiceShareText() {
        val customer = Customer(id = 1, name = "Gurpreet Singh", phone = "9876543210", village = "Kotli")
        val job = ServiceJob(
            id = 10,
            customerId = 1,
            invoiceNumber = "AL-2026-101",
            serviceType = ServiceType.HARVESTING,
            acres = 10.0,
            ratePerAcre = 2500.0,
            totalAmount = 25000.0,
            amountPaid = 15000.0,
            status = PaymentStatus.PARTIAL,
            notes = "Test harvest invoice"
        )

        val text = Formatters.generateInvoiceShareText(
            job = job,
            customer = customer,
            businessName = "AgriLedger Agro Services",
            currencySymbol = "₹"
        )

        assertTrue(text.contains("Gurpreet Singh"))
        assertTrue(text.contains("AL-2026-101"))
        assertTrue(text.contains("Combine Harvesting"))
        assertTrue(text.contains("PARTIAL"))
    }
}
