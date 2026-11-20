package com.agriledger.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.agriledger.app.data.database.dao.BusinessProfileDao
import com.agriledger.app.data.database.dao.CustomerDao
import com.agriledger.app.data.database.dao.ExpenseDao
import com.agriledger.app.data.database.dao.PaymentDao
import com.agriledger.app.data.database.dao.ServiceJobDao
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.ServiceJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Customer::class,
        ServiceJob::class,
        Payment::class,
        Expense::class,
        BusinessProfile::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AgriLedgerDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun serviceJobDao(): ServiceJobDao
    abstract fun paymentDao(): PaymentDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun businessProfileDao(): BusinessProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AgriLedgerDatabase? = null

        fun getInstance(context: Context): AgriLedgerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgriLedgerDatabase::class.java,
                    "agriledger_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Initialize default business profile
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.businessProfileDao()?.insertOrUpdateProfile(
                                    BusinessProfile(
                                        id = 1,
                                        businessName = "AgriLedger Agro Services",
                                        ownerName = "Farm Owner",
                                        phone = "+91 98765 43210",
                                        villageOrCity = "Green Valley Agritech Hub",
                                        state = "Punjab",
                                        currencySymbol = "₹",
                                        invoicePrefix = "AL-",
                                        taxOrGstNumber = ""
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
