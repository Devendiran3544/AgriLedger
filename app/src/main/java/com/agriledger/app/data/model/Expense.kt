package com.agriledger.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: ExpenseCategory,
    val amount: Double,
    val expenseDate: Long = System.currentTimeMillis(),
    val title: String,
    val notes: String = "",
    val receiptNumber: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
