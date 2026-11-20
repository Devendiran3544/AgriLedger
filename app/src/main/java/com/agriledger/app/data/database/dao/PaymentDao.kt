package com.agriledger.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agriledger.app.data.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY paymentDate DESC")
    fun getPaymentsForCustomer(customerId: Long): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE serviceJobId = :jobId ORDER BY paymentDate DESC")
    fun getPaymentsForJob(jobId: Long): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE id = :id")
    suspend fun getPaymentById(id: Long): Payment?

    @Query("SELECT * FROM payments ORDER BY paymentDate DESC LIMIT :limit")
    fun getRecentPayments(limit: Int = 10): Flow<List<Payment>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM payments")
    fun getTotalCollected(): Flow<Double>

    @Query("SELECT COUNT(*) FROM payments")
    fun getPaymentsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<Payment>): List<Long>

    @Update
    suspend fun updatePayment(payment: Payment)

    @Delete
    suspend fun deletePayment(payment: Payment)

    @Query("DELETE FROM payments")
    suspend fun deleteAllPayments()
}
