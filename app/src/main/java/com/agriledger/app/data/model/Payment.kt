package com.agriledger.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId"), Index("serviceJobId")]
)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val serviceJobId: Long? = null,
    val amount: Double,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val paymentDate: Long = System.currentTimeMillis(),
    val referenceNumber: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
