package com.agriledger.app.data.model

enum class ServiceType(val displayName: String, val defaultRatePerAcre: Double) {
    PLOUGHING("Ploughing / Tillage", 1200.0),
    HARVESTING("Combine Harvesting", 2500.0),
    SOWING("Seeding / Sowing", 900.0),
    ROTAVATOR("Rotavator / Cultivator", 1100.0),
    SPRAYING("Crop Spraying / Drone", 600.0),
    THRESHING("Threshing", 1500.0),
    LASER_LEVELING("Laser Land Leveling", 1800.0),
    TRANSPORTATION("Crop Transport", 1000.0),
    OTHER("Other Service", 1000.0)
}

enum class PaymentStatus(val displayName: String) {
    PAID("Paid"),
    PARTIAL("Partial"),
    UNPAID("Unpaid")
}

enum class PaymentMode(val displayName: String) {
    CASH("Cash"),
    UPI("UPI / Instant Pay"),
    BANK_TRANSFER("Bank Transfer / NEFT"),
    CHEQUE("Cheque"),
    OTHER("Other")
}

enum class ExpenseCategory(val displayName: String) {
    FUEL("Diesel / Fuel"),
    MAINTENANCE("Tractor / Machine Maintenance"),
    LABOR("Driver & Labor Wages"),
    SPARE_PARTS("Spare Parts & Repairs"),
    EQUIPMENT_RENT("Equipment / Attachment Rent"),
    LUBRICANTS_OIL("Engine Oil & Lubricants"),
    TRANSPORT("Transport / Tolls"),
    OTHER("General / Other Expense")
}
