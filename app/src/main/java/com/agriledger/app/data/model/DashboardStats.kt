package com.agriledger.app.data.model

data class DashboardStats(
    val totalCustomers: Int = 0,
    val totalAcresServiced: Double = 0.0,
    val totalBilled: Double = 0.0,
    val totalCollected: Double = 0.0,
    val totalPending: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netCashBalance: Double = 0.0
) {
    companion object {
        fun calculate(
            customersCount: Int,
            totalAcres: Double,
            totalBilled: Double,
            totalCollected: Double,
            totalPending: Double,
            totalExpenses: Double
        ): DashboardStats {
            // Net cash = Total collected minus total expenses (do not use unpaid invoices!)
            val netCash = totalCollected - totalExpenses
            return DashboardStats(
                totalCustomers = customersCount,
                totalAcresServiced = totalAcres,
                totalBilled = totalBilled,
                totalCollected = totalCollected,
                totalPending = totalPending,
                totalExpenses = totalExpenses,
                netCashBalance = netCash
            )
        }
    }
}

data class CustomerWithSummary(
    val customer: Customer,
    val totalAcresServiced: Double = 0.0,
    val totalBilled: Double = 0.0,
    val totalPaid: Double = 0.0,
    val totalPending: Double = 0.0,
    val serviceJobsCount: Int = 0
)

data class OutstandingCustomer(
    val customerId: Long,
    val customerName: String,
    val phone: String,
    val village: String,
    val pendingAmount: Double,
    val lastServiceDate: Long?
)

data class ExpenseCategoryStat(
    val category: ExpenseCategory,
    val totalAmount: Double,
    val percentage: Float
)

data class ServiceTypeStat(
    val serviceType: ServiceType,
    val totalAcres: Double,
    val totalBilled: Double,
    val jobCount: Int
)

data class FinancialReport(
    val periodName: String,
    val totalBilled: Double,
    val totalCollected: Double,
    val totalPending: Double,
    val totalExpenses: Double,
    val netCashFlow: Double,
    val totalAcresServiced: Double,
    val topExpenseCategories: List<ExpenseCategoryStat>,
    val serviceBreakdowns: List<ServiceTypeStat>
)
