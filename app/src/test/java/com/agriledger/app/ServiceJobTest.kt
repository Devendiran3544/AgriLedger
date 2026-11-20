package com.agriledger.app

import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.data.model.ServiceType
import org.junit.Assert.assertEquals
import org.junit.Test

class ServiceJobTest {

    @Test
    fun testPendingAmountCalculation() {
        val job = ServiceJob(
            customerId = 1,
            invoiceNumber = "AL-001",
            serviceType = ServiceType.ROTAVATOR,
            acres = 8.0,
            ratePerAcre = 1100.0,
            totalAmount = 8800.0,
            amountPaid = 3000.0,
            status = PaymentStatus.PARTIAL
        )

        assertEquals(5800.0, job.pendingAmount, 0.001)
    }

    @Test
    fun testPendingAmount_whenFullyPaid() {
        val job = ServiceJob(
            customerId = 1,
            invoiceNumber = "AL-002",
            serviceType = ServiceType.PLOUGHING,
            acres = 5.0,
            ratePerAcre = 1200.0,
            totalAmount = 6000.0,
            amountPaid = 6000.0,
            status = PaymentStatus.PAID
        )

        assertEquals(0.0, job.pendingAmount, 0.001)
    }
}
