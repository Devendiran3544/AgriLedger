package com.agriledger.app.data.database

import androidx.room.TypeConverter
import com.agriledger.app.data.model.ExpenseCategory
import com.agriledger.app.data.model.PaymentMode
import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.data.model.ServiceType

class Converters {
    @TypeConverter
    fun fromServiceType(value: ServiceType?): String? = value?.name

    @TypeConverter
    fun toServiceType(value: String?): ServiceType? =
        value?.let { runCatching { ServiceType.valueOf(it) }.getOrDefault(ServiceType.OTHER) }

    @TypeConverter
    fun fromPaymentStatus(value: PaymentStatus?): String? = value?.name

    @TypeConverter
    fun toPaymentStatus(value: String?): PaymentStatus? =
        value?.let { runCatching { PaymentStatus.valueOf(it) }.getOrDefault(PaymentStatus.UNPAID) }

    @TypeConverter
    fun fromPaymentMode(value: PaymentMode?): String? = value?.name

    @TypeConverter
    fun toPaymentMode(value: String?): PaymentMode? =
        value?.let { runCatching { PaymentMode.valueOf(it) }.getOrDefault(PaymentMode.CASH) }

    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory?): String? = value?.name

    @TypeConverter
    fun toExpenseCategory(value: String?): ExpenseCategory? =
        value?.let { runCatching { ExpenseCategory.valueOf(it) }.getOrDefault(ExpenseCategory.OTHER) }
}
