package com.agriledger.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.data.model.ServiceJob
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceJobDao {
    @Query("SELECT * FROM service_jobs ORDER BY serviceDate DESC")
    fun getAllServiceJobs(): Flow<List<ServiceJob>>

    @Query("SELECT * FROM service_jobs WHERE customerId = :customerId ORDER BY serviceDate DESC")
    fun getJobsForCustomer(customerId: Long): Flow<List<ServiceJob>>

    @Query("SELECT * FROM service_jobs WHERE id = :id")
    suspend fun getJobById(id: Long): ServiceJob?

    @Query("SELECT * FROM service_jobs WHERE status = :status ORDER BY serviceDate DESC")
    fun getJobsByStatus(status: PaymentStatus): Flow<List<ServiceJob>>

    @Query("SELECT * FROM service_jobs ORDER BY serviceDate DESC LIMIT :limit")
    fun getRecentJobs(limit: Int = 10): Flow<List<ServiceJob>>

    @Query("SELECT COALESCE(SUM(acres), 0.0) FROM service_jobs")
    fun getTotalAcres(): Flow<Double>

    @Query("SELECT COALESCE(SUM(totalAmount), 0.0) FROM service_jobs")
    fun getTotalBilled(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amountPaid), 0.0) FROM service_jobs")
    fun getTotalBilledPaid(): Flow<Double>

    @Query("SELECT COALESCE(SUM(totalAmount - amountPaid), 0.0) FROM service_jobs WHERE totalAmount > amountPaid")
    fun getTotalPending(): Flow<Double>

    @Query("SELECT COUNT(*) FROM service_jobs")
    fun getJobsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: ServiceJob): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<ServiceJob>): List<Long>

    @Update
    suspend fun updateJob(job: ServiceJob)

    @Delete
    suspend fun deleteJob(job: ServiceJob)

    @Query("DELETE FROM service_jobs")
    suspend fun deleteAllJobs()
}
