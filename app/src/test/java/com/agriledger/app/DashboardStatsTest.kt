package com.agriledger.app

import com.agriledger.app.data.model.DashboardStats
import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardStatsTest {

    @Test
    fun testNetCashCalculation_excludesUnpaidInvoices() {
        val totalBilled = 100000.0
        val totalCollected = 60000.0 // only 60k collected
        val totalPending = 40000.0   // 40k pending
        val totalExpenses = 25000.0  // 25k spent on fuel & wages

        val stats = DashboardStats.calculate(
            customersCount = 5,
            totalAcres = 45.0,
            totalBilled = totalBilled,
            totalCollected = totalCollected,
            totalPending = totalPending,
            totalExpenses = totalExpenses
        )

        // Net cash MUST be totalCollected - totalExpenses = 60,000 - 25,000 = 35,000
        // (NOT totalBilled - totalExpenses = 75,000)
        assertEquals(35000.0, stats.netCashBalance, 0.001)
        assertEquals(totalBilled, stats.totalBilled, 0.001)
        assertEquals(totalCollected, stats.totalCollected, 0.001)
        assertEquals(totalPending, stats.totalPending, 0.001)
        assertEquals(totalExpenses, stats.totalExpenses, 0.001)
        assertEquals(5, stats.totalCustomers)
        assertEquals(45.0, stats.totalAcresServiced, 0.001)
    }

    @Test
    fun testNetCashCalculation_whenExpensesExceedCollections() {
        val stats = DashboardStats.calculate(
            customersCount = 2,
            totalAcres = 10.0,
            totalBilled = 20000.0,
            totalCollected = 5000.0,
            totalPending = 15000.0,
            totalExpenses = 8000.0
        )

        // Net cash is 5000 - 8000 = -3000
        assertEquals(-3000.0, stats.netCashBalance, 0.001)
    }
}
