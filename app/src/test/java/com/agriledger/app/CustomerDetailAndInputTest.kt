package com.agriledger.app

import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.data.model.ServiceType
import com.agriledger.app.ui.customers.CustomerDetailUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerDetailAndInputTest {

    @Test
    fun testCustomerDetailUiStateTransitions() {
        val loadingState: CustomerDetailUiState = CustomerDetailUiState.Loading
        assertTrue(loadingState is CustomerDetailUiState.Loading)

        val errorState: CustomerDetailUiState = CustomerDetailUiState.Error("Customer not found")
        assertTrue(errorState is CustomerDetailUiState.Error)
        assertEquals("Customer not found", (errorState as CustomerDetailUiState.Error).message)

        val customer = Customer(id = 5L, name = "Harpreet Singh", phone = "9876543210", village = "Kotli")
        val jobs = listOf(
            ServiceJob(
                id = 1L,
                customerId = 5L,
                invoiceNumber = "INV-001",
                serviceType = ServiceType.HARVESTING,
                acres = 10.0,
                ratePerAcre = 2500.0,
                totalAmount = 25000.0
            )
        )
        val payments = emptyList<Payment>()

        val successState: CustomerDetailUiState = CustomerDetailUiState.Success(customer, jobs, payments)
        assertTrue(successState is CustomerDetailUiState.Success)
        val success = successState as CustomerDetailUiState.Success
        assertEquals("Harpreet Singh", success.customer.name)
        assertEquals(1, success.jobs.size)
        assertEquals(25000.0, success.jobs.sumOf { it.totalAmount }, 0.001)
    }

    @Test
    fun testAmountInputSanitizationAndValidation() {
        // Test comma to dot replacement (common in Indian & European keypads)
        val rawInputWithComma = "4500,50"
        val sanitized = rawInputWithComma.trim().replace(',', '.')
        assertEquals("4500.50", sanitized)
        val parsed = sanitized.toDoubleOrNull()
        assertEquals(4500.50, parsed ?: 0.0, 0.001)

        // Test decimal regex matching
        val decimalRegex = Regex("^\\d*\\.?\\d*$")
        assertTrue("".matches(decimalRegex))
        assertTrue("100".matches(decimalRegex))
        assertTrue("100.".matches(decimalRegex))
        assertTrue("100.5".matches(decimalRegex))
        assertTrue("100.50".matches(decimalRegex))
        assertTrue(!"100.50.2".matches(decimalRegex))
        assertTrue(!"-50".matches(decimalRegex))
        assertTrue(!"abc".matches(decimalRegex))

        // Test validation logic
        val validAmount = "2500".trim().replace(',', '.').toDoubleOrNull()
        assertTrue(validAmount != null && validAmount > 0.0)

        val zeroAmount = "0".trim().replace(',', '.').toDoubleOrNull()
        assertTrue(zeroAmount != null && zeroAmount <= 0.0)

        val emptyAmount = "".trim().replace(',', '.').toDoubleOrNull()
        assertTrue(emptyAmount == null)
    }
}
