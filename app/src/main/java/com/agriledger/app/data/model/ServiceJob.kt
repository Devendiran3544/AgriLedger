package com.agriledger.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_jobs",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId")]
)
data class ServiceJob(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val invoiceNumber: String,
    val serviceType: ServiceType,
    val acres: Double,
    val ratePerAcre: Double,
    val totalAmount: Double,
    val amountPaid: Double = 0.0,
    val status: PaymentStatus = PaymentStatus.UNPAID,
    val serviceDate: Long = System.currentTimeMillis(),
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val pendingAmount: Double
        get() = (totalAmount - amountPaid).coerceAtLeast(0.0)
}
