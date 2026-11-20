package com.agriledger.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY expenseDate DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY expenseDate DESC")
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?

    @Query("SELECT * FROM expenses ORDER BY expenseDate DESC LIMIT :limit")
    fun getRecentExpenses(limit: Int = 10): Flow<List<Expense>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses")
    fun getTotalExpenses(): Flow<Double>

    @Query("SELECT category, COALESCE(SUM(amount), 0.0) as total FROM expenses GROUP BY category")
    fun getCategoryExpenseSums(): Flow<List<CategorySum>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<Expense>): List<Long>

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}

data class CategorySum(
    val category: ExpenseCategory,
    val total: Double
)
