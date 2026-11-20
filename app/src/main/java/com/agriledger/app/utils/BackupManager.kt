package com.agriledger.app.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.room.withTransaction
import com.agriledger.app.data.database.AgriLedgerDatabase
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.ExpenseCategory
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.PaymentMode
import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.data.model.ServiceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupManager {

    /**
     * Exports the complete AgriLedger database to a structured JSON string.
     */
    suspend fun exportDatabaseToJson(database: AgriLedgerDatabase): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "AgriLedger")
        root.put("version", 1)
        root.put("exportTimestamp", System.currentTimeMillis())
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date()))

        // 1. Business Profile
        val profile = database.businessProfileDao().getBusinessProfileOnce() ?: BusinessProfile()
        val profileJson = JSONObject().apply {
            put("id", profile.id)
            put("businessName", profile.businessName)
            put("ownerName", profile.ownerName)
            put("phone", profile.phone)
            put("villageOrCity", profile.villageOrCity)
            put("state", profile.state)
            put("currencySymbol", profile.currencySymbol)
            put("invoicePrefix", profile.invoicePrefix)
            put("taxOrGstNumber", profile.taxOrGstNumber)
        }
        root.put("businessProfile", profileJson)

        // 2. Customers
        val customers = database.customerDao().getAllCustomers().first()
        val customersArray = JSONArray()
        customers.forEach { c ->
            val cJson = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("phone", c.phone)
                put("village", c.village)
                put("totalLandAcres", c.totalLandAcres)
                put("notes", c.notes)
                put("createdAt", c.createdAt)
            }
            customersArray.put(cJson)
        }
        root.put("customers", customersArray)

        // 3. Service Jobs / Bills
        val jobs = database.serviceJobDao().getAllServiceJobs().first()
        val jobsArray = JSONArray()
        jobs.forEach { j ->
            val jJson = JSONObject().apply {
                put("id", j.id)
                put("customerId", j.customerId)
                put("invoiceNumber", j.invoiceNumber)
                put("serviceType", j.serviceType.name)
                put("acres", j.acres)
                put("ratePerAcre", j.ratePerAcre)
                put("totalAmount", j.totalAmount)
                put("amountPaid", j.amountPaid)
                put("status", j.status.name)
                put("serviceDate", j.serviceDate)
                put("notes", j.notes)
                put("createdAt", j.createdAt)
            }
            jobsArray.put(jJson)
        }
        root.put("serviceJobs", jobsArray)

        // 4. Payments
        val payments = database.paymentDao().getAllPayments().first()
        val paymentsArray = JSONArray()
        payments.forEach { p ->
            val pJson = JSONObject().apply {
                put("id", p.id)
                put("customerId", p.customerId)
                if (p.serviceJobId != null) put("serviceJobId", p.serviceJobId)
                put("amount", p.amount)
                put("paymentMode", p.paymentMode.name)
                put("paymentDate", p.paymentDate)
                put("referenceNumber", p.referenceNumber)
                put("notes", p.notes)
                put("createdAt", p.createdAt)
            }
            paymentsArray.put(pJson)
        }
        root.put("payments", paymentsArray)

        // 5. Expenses
        val expenses = database.expenseDao().getAllExpenses().first()
        val expensesArray = JSONArray()
        expenses.forEach { e ->
            val eJson = JSONObject().apply {
                put("id", e.id)
                put("category", e.category.name)
                put("amount", e.amount)
                put("expenseDate", e.expenseDate)
                put("title", e.title)
                put("notes", e.notes)
                put("receiptNumber", e.receiptNumber)
                put("createdAt", e.createdAt)
            }
            expensesArray.put(eJson)
        }
        root.put("expenses", expensesArray)

        root.toString(2)
    }

    /**
     * Exports database to a JSON file and prompts the user with Android Share / Save Sheet.
     */
    suspend fun exportAndShareBackup(context: Context, database: AgriLedgerDatabase) {
        val json = exportDatabaseToJson(database)
        val backupDir = File(context.cacheDir, "backups").apply { if (!exists()) mkdirs() }
        val dateSuffix = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val backupFile = File(backupDir, "AgriLedger_Backup_${dateSuffix}.json")

        FileOutputStream(backupFile).use { out ->
            out.write(json.toByteArray(Charsets.UTF_8))
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            backupFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "AgriLedger Complete Database Backup - $dateSuffix")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Save / Export Backup File").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    /**
     * Restores full database state from a validated JSON backup string.
     */
    suspend fun restoreDatabaseFromJson(database: AgriLedgerDatabase, jsonString: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val root = JSONObject(jsonString)
            if (!root.has("app") || root.getString("app") != "AgriLedger") {
                error("Invalid backup file: AgriLedger signature not found.")
            }

            database.withTransaction {
                // 1. Wipe current tables
                database.paymentDao().deleteAllPayments()
                database.serviceJobDao().deleteAllJobs()
                database.customerDao().deleteAllCustomers()
                database.expenseDao().deleteAllExpenses()

                // 2. Restore Business Profile
                if (root.has("businessProfile")) {
                    val pJson = root.getJSONObject("businessProfile")
                    val profile = BusinessProfile(
                        id = pJson.optInt("id", 1),
                        businessName = pJson.optString("businessName", "AgriLedger Agro Services"),
                        ownerName = pJson.optString("ownerName", "Farm Owner"),
                        phone = pJson.optString("phone", ""),
                        villageOrCity = pJson.optString("villageOrCity", ""),
                        state = pJson.optString("state", ""),
                        currencySymbol = pJson.optString("currencySymbol", "₹"),
                        invoicePrefix = pJson.optString("invoicePrefix", "AL-"),
                        taxOrGstNumber = pJson.optString("taxOrGstNumber", "")
                    )
                    database.businessProfileDao().insertOrUpdateProfile(profile)
                }

                // 3. Restore Customers
                val customersArray = root.optJSONArray("customers") ?: JSONArray()
                val restoredCustomers = mutableListOf<Customer>()
                for (i in 0 until customersArray.length()) {
                    val cJson = customersArray.getJSONObject(i)
                    restoredCustomers.add(
                        Customer(
                            id = cJson.optLong("id", 0L),
                            name = cJson.getString("name"),
                            phone = cJson.getString("phone"),
                            village = cJson.optString("village", ""),
                            totalLandAcres = cJson.optDouble("totalLandAcres", 0.0),
                            notes = cJson.optString("notes", ""),
                            createdAt = cJson.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                if (restoredCustomers.isNotEmpty()) {
                    database.customerDao().insertCustomers(restoredCustomers)
                }

                // 4. Restore Service Jobs
                val jobsArray = root.optJSONArray("serviceJobs") ?: JSONArray()
                val restoredJobs = mutableListOf<ServiceJob>()
                for (i in 0 until jobsArray.length()) {
                    val jJson = jobsArray.getJSONObject(i)
                    val serviceTypeStr = jJson.optString("serviceType", "OTHER")
                    val serviceType = runCatching { ServiceType.valueOf(serviceTypeStr) }.getOrDefault(ServiceType.OTHER)
                    val statusStr = jJson.optString("status", "UNPAID")
                    val status = runCatching { PaymentStatus.valueOf(statusStr) }.getOrDefault(PaymentStatus.UNPAID)

                    restoredJobs.add(
                        ServiceJob(
                            id = jJson.optLong("id", 0L),
                            customerId = jJson.getLong("customerId"),
                            invoiceNumber = jJson.getString("invoiceNumber"),
                            serviceType = serviceType,
                            acres = jJson.getDouble("acres"),
                            ratePerAcre = jJson.getDouble("ratePerAcre"),
                            totalAmount = jJson.getDouble("totalAmount"),
                            amountPaid = jJson.optDouble("amountPaid", 0.0),
                            status = status,
                            serviceDate = jJson.optLong("serviceDate", System.currentTimeMillis()),
                            notes = jJson.optString("notes", ""),
                            createdAt = jJson.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                if (restoredJobs.isNotEmpty()) {
                    database.serviceJobDao().insertJobs(restoredJobs)
                }

                // 5. Restore Payments
                val paymentsArray = root.optJSONArray("payments") ?: JSONArray()
                val restoredPayments = mutableListOf<Payment>()
                for (i in 0 until paymentsArray.length()) {
                    val pJson = paymentsArray.getJSONObject(i)
                    val modeStr = pJson.optString("paymentMode", "CASH")
                    val mode = runCatching { PaymentMode.valueOf(modeStr) }.getOrDefault(PaymentMode.CASH)
                    val jobId = if (pJson.has("serviceJobId")) pJson.getLong("serviceJobId") else null

                    restoredPayments.add(
                        Payment(
                            id = pJson.optLong("id", 0L),
                            customerId = pJson.getLong("customerId"),
                            serviceJobId = jobId,
                            amount = pJson.getDouble("amount"),
                            paymentMode = mode,
                            paymentDate = pJson.optLong("paymentDate", System.currentTimeMillis()),
                            referenceNumber = pJson.optString("referenceNumber", ""),
                            notes = pJson.optString("notes", ""),
                            createdAt = pJson.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                if (restoredPayments.isNotEmpty()) {
                    database.paymentDao().insertPayments(restoredPayments)
                }

                // 6. Restore Expenses
                val expensesArray = root.optJSONArray("expenses") ?: JSONArray()
                val restoredExpenses = mutableListOf<Expense>()
                for (i in 0 until expensesArray.length()) {
                    val eJson = expensesArray.getJSONObject(i)
                    val categoryStr = eJson.optString("category", "OTHER")
                    val category = runCatching { ExpenseCategory.valueOf(categoryStr) }.getOrDefault(ExpenseCategory.OTHER)

                    restoredExpenses.add(
                        Expense(
                            id = eJson.optLong("id", 0L),
                            category = category,
                            amount = eJson.getDouble("amount"),
                            expenseDate = eJson.optLong("expenseDate", System.currentTimeMillis()),
                            title = eJson.getString("title"),
                            notes = eJson.optString("notes", ""),
                            receiptNumber = eJson.optString("receiptNumber", ""),
                            createdAt = eJson.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                if (restoredExpenses.isNotEmpty()) {
                    database.expenseDao().insertExpenses(restoredExpenses)
                }
            }
        }
    }
}
