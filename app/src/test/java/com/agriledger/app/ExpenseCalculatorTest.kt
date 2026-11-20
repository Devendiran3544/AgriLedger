package com.agriledger.app

import com.agriledger.app.ui.expenses.CalculatorLineItem
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpenseCalculatorTest {

    @Test
    fun testCalculatorLineItemsTotal() {
        val items = listOf(
            CalculatorLineItem(name = "50L Diesel", amount = 4500.0),
            CalculatorLineItem(name = "Engine Oil 7.5L", amount = 3200.0),
            CalculatorLineItem(name = "Hydraulic Filter", amount = 1450.0),
            CalculatorLineItem(name = "Driver Batta", amount = 1200.0)
        )

        val total = items.sumOf { it.amount }
        assertEquals(10350.0, total, 0.001)
        assertEquals(4, items.size)
    }

    @Test
    fun testCalculatorItemUpdateAndRemoval() {
        val items = mutableListOf(
            CalculatorLineItem(id = 1L, name = "Shovel points", amount = 2500.0),
            CalculatorLineItem(id = 2L, name = "Grease Can", amount = 600.0)
        )

        // Update item 1
        items[0] = CalculatorLineItem(id = 1L, name = "Hardened Shovel points", amount = 2800.0)
        assertEquals(3400.0, items.sumOf { it.amount }, 0.001)

        // Remove item 2
        items.removeIf { it.id == 2L }
        assertEquals(2800.0, items.sumOf { it.amount }, 0.001)
        assertEquals(1, items.size)
    }
}
