package com.agriledger.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey
    val id: Int = 1,
    val businessName: String = "AgriLedger Agro Services",
    val ownerName: String = "Farm Owner",
    val phone: String = "+91 98765 43210",
    val villageOrCity: String = "Green Valley Agritech Hub",
    val state: String = "Punjab",
    val currencySymbol: String = "₹",
    val invoicePrefix: String = "AL-",
    val taxOrGstNumber: String = ""
)
