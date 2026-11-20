package com.agriledger.app.data.repository

import com.agriledger.app.data.database.AgriLedgerDatabase
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.CustomerWithSummary
import com.agriledger.app.data.model.DashboardStats
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.ExpenseCategory
import com.agriledger.app.data.model.ExpenseCategoryStat
import com.agriledger.app.data.model.FinancialReport
import com.agriledger.app.data.model.OutstandingCustomer
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.PaymentMode
import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.data.model.ServiceType
import com.agriledger.app.data.model.ServiceTypeStat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class AgriLedgerRepository(private val database: AgriLedgerDatabase) {

    private val customerDao = database.customerDao()
    private val serviceJobDao = database.serviceJobDao()
    private val paymentDao = database.paymentDao()
    private val expenseDao = database.expenseDao()
    private val businessProfileDao = database.businessProfileDao()

    // Customers Flow
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()

    // Service Jobs Flow
    val allServiceJobs: Flow<List<ServiceJob>> = serviceJobDao.getAllServiceJobs()

    // Payments Flow
    val allPayments: Flow<List<Payment>> = paymentDao.getAllPayments()

    // Expenses Flow
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    // Business Profile Flow
    val businessProfile: Flow<BusinessProfile?> = businessProfileDao.getBusinessProfile()

    // Recent items for Dashboard
    val recentJobs: Flow<List<ServiceJob>> = serviceJobDao.getRecentJobs(8)
    val recentPayments: Flow<List<Payment>> = paymentDao.getRecentPayments(8)
    val recentExpenses: Flow<List<Expense>> = expenseDao.getRecentExpenses(8)

    // Customer With Financial Summary Flow
    val customersWithSummary: Flow<List<CustomerWithSummary>> = combine(
        allCustomers,
        allServiceJobs,
        allPayments
    ) { customers, jobs, payments ->
        customers.map { customer ->
            val custJobs = jobs.filter { it.customerId == customer.id }
            val custPayments = payments.filter { it.customerId == customer.id }

            val totalAcres = custJobs.sumOf { it.acres }
            val totalBilled = custJobs.sumOf { it.totalAmount }
            val totalPaid = custPayments.sumOf { it.amount }
            val totalPending = (totalBilled - totalPaid).coerceAtLeast(0.0)

            CustomerWithSummary(
                customer = customer,
                totalAcresServiced = totalAcres,
                totalBilled = totalBilled,
                totalPaid = totalPaid,
                totalPending = totalPending,
                serviceJobsCount = custJobs.size
            )
        }
    }.flowOn(Dispatchers.Default)

    // Outstanding Customers (who owe money)
    val outstandingCustomers: Flow<List<OutstandingCustomer>> = combine(
        customersWithSummary,
        allServiceJobs
    ) { summaries, jobs ->
        summaries
            .filter { it.totalPending > 0.0 }
            .map { summary ->
                val lastJob = jobs.filter { it.customerId == summary.customer.id }
                    .maxByOrNull { it.serviceDate }
                OutstandingCustomer(
                    customerId = summary.customer.id,
                    customerName = summary.customer.name,
                    phone = summary.customer.phone,
                    village = summary.customer.village,
                    pendingAmount = summary.totalPending,
                    lastServiceDate = lastJob?.serviceDate
                )
            }
            .sortedByDescending { it.pendingAmount }
    }.flowOn(Dispatchers.Default)

    // Comprehensive Dashboard Stats Flow
    // Calculations as strictly requested:
    // Total billed: Sum of all customer bills
    // Total collected: Sum of actual payments received
    // Total pending: Sum of outstanding customer balances
    // Total expenses: Sum of saved expenses
    // Net cash: Total collected minus total expenses (do NOT calculate net cash using unpaid invoices)
    val dashboardStats: Flow<DashboardStats> = combine(
        allCustomers,
        allServiceJobs,
        allPayments,
        allExpenses
    ) { customers, jobs, payments, expenses ->
        val totalAcres = jobs.sumOf { it.acres }
        val totalBilled = jobs.sumOf { it.totalAmount }
        val totalCollected = payments.sumOf { it.amount }
        val totalPending = (totalBilled - totalCollected).coerceAtLeast(0.0)
        val totalExpenses = expenses.sumOf { it.amount }

        DashboardStats.calculate(
            customersCount = customers.size,
            totalAcres = totalAcres,
            totalBilled = totalBilled,
            totalCollected = totalCollected,
            totalPending = totalPending,
            totalExpenses = totalExpenses
        )
    }.flowOn(Dispatchers.Default)

    // Reports Flow
    val financialReport: Flow<FinancialReport> = combine(
        allServiceJobs,
        allPayments,
        allExpenses
    ) { jobs, payments, expenses ->
        val totalAcres = jobs.sumOf { it.acres }
        val totalBilled = jobs.sumOf { it.totalAmount }
        val totalCollected = payments.sumOf { it.amount }
        val totalPending = (totalBilled - totalCollected).coerceAtLeast(0.0)
        val totalExpenses = expenses.sumOf { it.amount }
        val netCash = totalCollected - totalExpenses

        // Category stats for expenses
        val catMap = expenses.groupBy { it.category }
        val catStats = ExpenseCategory.entries.mapNotNull { cat ->
            val list = catMap[cat] ?: return@mapNotNull null
            val sum = list.sumOf { it.amount }
            if (sum > 0) {
                ExpenseCategoryStat(
                    category = cat,
                    totalAmount = sum,
                    percentage = if (totalExpenses > 0) (sum / totalExpenses * 100).toFloat() else 0f
                )
            } else null
        }.sortedByDescending { it.totalAmount }

        // Service type breakdown
        val serviceMap = jobs.groupBy { it.serviceType }
        val serviceStats = ServiceType.entries.mapNotNull { st ->
            val list = serviceMap[st] ?: return@mapNotNull null
            val acres = list.sumOf { it.acres }
            val billed = list.sumOf { it.totalAmount }
            if (billed > 0 || acres > 0) {
                ServiceTypeStat(
                    serviceType = st,
                    totalAcres = acres,
                    totalBilled = billed,
                    jobCount = list.size
                )
            } else null
        }.sortedByDescending { it.totalBilled }

        FinancialReport(
            periodName = "Overall Summary",
            totalBilled = totalBilled,
            totalCollected = totalCollected,
            totalPending = totalPending,
            totalExpenses = totalExpenses,
            netCashFlow = netCash,
            totalAcresServiced = totalAcres,
            topExpenseCategories = catStats,
            serviceBreakdowns = serviceStats
        )
    }.flowOn(Dispatchers.Default)

    // Customer Operations
    suspend fun getCustomerById(id: Long): Customer? = withContext(Dispatchers.IO) {
        customerDao.getCustomerById(id)
    }

    fun getCustomerByIdFlow(id: Long): Flow<Customer?> = customerDao.getCustomerByIdFlow(id)

    fun getJobsForCustomer(customerId: Long): Flow<List<ServiceJob>> =
        serviceJobDao.getJobsForCustomer(customerId)

    fun getPaymentsForCustomer(customerId: Long): Flow<List<Payment>> =
        paymentDao.getPaymentsForCustomer(customerId)

    suspend fun insertCustomer(customer: Customer): Long = withContext(Dispatchers.IO) {
        customerDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer) = withContext(Dispatchers.IO) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) = withContext(Dispatchers.IO) {
        customerDao.deleteCustomer(customer)
    }

    // Service Job Operations
    suspend fun createServiceJob(job: ServiceJob): Long = withContext(Dispatchers.IO) {
        // Generate clean sequential invoice number if blank
        val invoiceNum = if (job.invoiceNumber.isBlank()) {
            val count = serviceJobDao.getJobsCount().first()
            "INV-${System.currentTimeMillis() % 100000}-${count + 1}"
        } else job.invoiceNumber

        val status = when {
            job.amountPaid >= job.totalAmount -> PaymentStatus.PAID
            job.amountPaid > 0 -> PaymentStatus.PARTIAL
            else -> PaymentStatus.UNPAID
        }

        val updatedJob = job.copy(
            invoiceNumber = invoiceNum,
            status = status
        )
        serviceJobDao.insertJob(updatedJob)
    }

    suspend fun updateServiceJob(job: ServiceJob) = withContext(Dispatchers.IO) {
        val status = when {
            job.amountPaid >= job.totalAmount -> PaymentStatus.PAID
            job.amountPaid > 0 -> PaymentStatus.PARTIAL
            else -> PaymentStatus.UNPAID
        }
        serviceJobDao.updateJob(job.copy(status = status))
    }

    suspend fun deleteServiceJob(job: ServiceJob) = withContext(Dispatchers.IO) {
        serviceJobDao.deleteJob(job)
    }

    // Payment Operations
    suspend fun recordPayment(payment: Payment): Long = withContext(Dispatchers.IO) {
        val paymentId = paymentDao.insertPayment(payment)

        // Apply to specific job if selected
        if (payment.serviceJobId != null) {
            val job = serviceJobDao.getJobById(payment.serviceJobId)
            if (job != null) {
                val newPaid = job.amountPaid + payment.amount
                val newStatus = when {
                    newPaid >= job.totalAmount -> PaymentStatus.PAID
                    newPaid > 0 -> PaymentStatus.PARTIAL
                    else -> PaymentStatus.UNPAID
                }
                serviceJobDao.updateJob(job.copy(amountPaid = newPaid, status = newStatus))
            }
        } else {
            // Apply payment FIFO against unpaid or partial service jobs of this customer
            var remainingPayment = payment.amount
            val customerJobs = serviceJobDao.getJobsForCustomer(payment.customerId).first()
                .filter { it.status != PaymentStatus.PAID }
                .sortedBy { it.serviceDate }

            for (job in customerJobs) {
                if (remainingPayment <= 0.0) break
                val due = (job.totalAmount - job.amountPaid).coerceAtLeast(0.0)
                val paymentForThisJob = minOf(remainingPayment, due)
                val newPaid = job.amountPaid + paymentForThisJob
                val newStatus = if (newPaid >= job.totalAmount) PaymentStatus.PAID else PaymentStatus.PARTIAL
                serviceJobDao.updateJob(job.copy(amountPaid = newPaid, status = newStatus))
                remainingPayment -= paymentForThisJob
            }
        }

        paymentId
    }

    suspend fun deletePayment(payment: Payment) = withContext(Dispatchers.IO) {
        paymentDao.deletePayment(payment)
    }

    // Expense Operations
    suspend fun insertExpense(expense: Expense): Long = withContext(Dispatchers.IO) {
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) = withContext(Dispatchers.IO) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) = withContext(Dispatchers.IO) {
        expenseDao.deleteExpense(expense)
    }

    // Business Profile & Settings
    suspend fun getBusinessProfileOnce(): BusinessProfile {
        return withContext(Dispatchers.IO) {
            businessProfileDao.getBusinessProfileOnce() ?: BusinessProfile()
        }
    }

    suspend fun saveBusinessProfile(profile: BusinessProfile) = withContext(Dispatchers.IO) {
        businessProfileDao.insertOrUpdateProfile(profile)
    }

    // Clear all data
    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        paymentDao.deleteAllPayments()
        serviceJobDao.deleteAllJobs()
        customerDao.deleteAllCustomers()
        expenseDao.deleteAllExpenses()
    }
}
