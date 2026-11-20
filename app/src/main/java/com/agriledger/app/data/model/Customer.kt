package com.agriledger.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val village: String = "",
    val totalLandAcres: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
