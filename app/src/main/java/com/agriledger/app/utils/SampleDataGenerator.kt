package com.agriledger.app.utils

import com.agriledger.app.data.database.AgriLedgerDatabase
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.ExpenseCategory
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.PaymentMode
import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.data.model.ServiceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SampleDataGenerator {

    suspend fun seedDatabase(database: AgriLedgerDatabase) = withContext(Dispatchers.IO) {
        val customerDao = database.customerDao()
        val serviceJobDao = database.serviceJobDao()
        val paymentDao = database.paymentDao()
        val expenseDao = database.expenseDao()

        // 1. Insert Customers
        val customers = listOf(
            Customer(name = "Sardar Gurpreet Singh", phone = "9872145610", village = "Kotli Kalan", totalLandAcres = 28.5, notes = "Large wheat & paddy grower. Prefers UPI."),
            Customer(name = "Ramesh Bhai Patel", phone = "9825012345", village = "Navagam", totalLandAcres = 18.0, notes = "Prompt payer. Cotton farming."),
            Customer(name = "Vikram Patil", phone = "9422078912", village = "Shirpur", totalLandAcres = 14.0, notes = "Requires rotavator and laser leveling before monsoons."),
            Customer(name = "Baldev Singh Sandhu", phone = "9814032198", village = "Rampura", totalLandAcres = 40.0, notes = "Key commercial client. Custom harvesting."),
            Customer(name = "Rajesh Sharma", phone = "9419165432", village = "Sundernagar", totalLandAcres = 8.5, notes = "Small orchard and fodder field."),
            Customer(name = "Mohanlal Choudhary", phone = "9784011223", village = "Kishangarh", totalLandAcres = 22.0, notes = "Mustard and wheat producer.")
        )

        val customerIds = customerDao.insertCustomers(customers)
        val now = System.currentTimeMillis()
        val dayMillis = 24L * 60 * 60 * 1000

        // 2. Insert Service Jobs
        val jobs = listOf(
            // Gurpreet Singh
            ServiceJob(
                customerId = customerIds[0],
                invoiceNumber = "AL-2026-001",
                serviceType = ServiceType.HARVESTING,
                acres = 15.0,
                ratePerAcre = 2400.0,
                totalAmount = 36000.0,
                amountPaid = 36000.0,
                status = PaymentStatus.PAID,
                serviceDate = now - (14 * dayMillis),
                notes = "Claas Harvester, wheat harvest complete"
            ),
            ServiceJob(
                customerId = customerIds[0],
                invoiceNumber = "AL-2026-002",
                serviceType = ServiceType.ROTAVATOR,
                acres = 12.0,
                ratePerAcre = 1100.0,
                totalAmount = 13200.0,
                amountPaid = 5000.0,
                status = PaymentStatus.PARTIAL,
                serviceDate = now - (6 * dayMillis),
                notes = "Pre-sowing field preparation"
            ),
            // Ramesh Patel
            ServiceJob(
                customerId = customerIds[1],
                invoiceNumber = "AL-2026-003",
                serviceType = ServiceType.PLOUGHING,
                acres = 10.0,
                ratePerAcre = 1200.0,
                totalAmount = 12000.0,
                amountPaid = 12000.0,
                status = PaymentStatus.PAID,
                serviceDate = now - (20 * dayMillis),
                notes = "Deep summer tillage"
            ),
            // Vikram Patil
            ServiceJob(
                customerId = customerIds[2],
                invoiceNumber = "AL-2026-004",
                serviceType = ServiceType.LASER_LEVELING,
                acres = 8.0,
                ratePerAcre = 1800.0,
                totalAmount = 14400.0,
                amountPaid = 0.0,
                status = PaymentStatus.UNPAID,
                serviceDate = now - (3 * dayMillis),
                notes = "Precision laser land leveling"
            ),
            // Baldev Singh Sandhu
            ServiceJob(
                customerId = customerIds[3],
                invoiceNumber = "AL-2026-005",
                serviceType = ServiceType.HARVESTING,
                acres = 25.0,
                ratePerAcre = 2500.0,
                totalAmount = 62500.0,
                amountPaid = 40000.0,
                status = PaymentStatus.PARTIAL,
                serviceDate = now - (10 * dayMillis),
                notes = "Paddy harvest with straw chopper"
            ),
            // Rajesh Sharma
            ServiceJob(
                customerId = customerIds[4],
                invoiceNumber = "AL-2026-006",
                serviceType = ServiceType.SPRAYING,
                acres = 6.0,
                ratePerAcre = 700.0,
                totalAmount = 4200.0,
                amountPaid = 0.0,
                status = PaymentStatus.UNPAID,
                serviceDate = now - (1 * dayMillis),
                notes = "Drone pesticide foliar spray"
            ),
            // Mohanlal Choudhary
            ServiceJob(
                customerId = customerIds[5],
                invoiceNumber = "AL-2026-007",
                serviceType = ServiceType.SOWING,
                acres = 14.0,
                ratePerAcre = 950.0,
                totalAmount = 13300.0,
                amountPaid = 13300.0,
                status = PaymentStatus.PAID,
                serviceDate = now - (18 * dayMillis),
                notes = "Zero-till seed drill sowing"
            )
        )
        val jobIds = serviceJobDao.insertJobs(jobs)

        // 3. Insert Payments
        val payments = listOf(
            Payment(
                customerId = customerIds[0],
                serviceJobId = jobIds[0],
                amount = 36000.0,
                paymentMode = PaymentMode.UPI,
                paymentDate = now - (12 * dayMillis),
                referenceNumber = "UPI-739102481",
                notes = "Full payment for harvesting"
            ),
            Payment(
                customerId = customerIds[0],
                serviceJobId = jobIds[1],
                amount = 5000.0,
                paymentMode = PaymentMode.CASH,
                paymentDate = now - (5 * dayMillis),
                referenceNumber = "REC-104",
                notes = "Advance payment for rotavator"
            ),
            Payment(
                customerId = customerIds[1],
                serviceJobId = jobIds[2],
                amount = 12000.0,
                paymentMode = PaymentMode.BANK_TRANSFER,
                paymentDate = now - (19 * dayMillis),
                referenceNumber = "NEFT-99120481",
                notes = "Payment via HDFC net banking"
            ),
            Payment(
                customerId = customerIds[3],
                serviceJobId = jobIds[4],
                amount = 40000.0,
                paymentMode = PaymentMode.CHEQUE,
                paymentDate = now - (8 * dayMillis),
                referenceNumber = "CHQ-481029",
                notes = "Part payment received via SBI cheque"
            ),
            Payment(
                customerId = customerIds[5],
                serviceJobId = jobIds[6],
                amount = 13300.0,
                paymentMode = PaymentMode.CASH,
                paymentDate = now - (17 * dayMillis),
                referenceNumber = "REC-101",
                notes = "Paid in cash after job completion"
            )
        )
        paymentDao.insertPayments(payments)

        // 4. Insert Expenses
        val expenses = listOf(
            Expense(
                category = ExpenseCategory.FUEL,
                amount = 14500.0,
                expenseDate = now - (13 * dayMillis),
                title = "150L Diesel for Harvester",
                notes = "Indian Oil Petrol Pump, Highway Station",
                receiptNumber = "IOC-84910"
            ),
            Expense(
                category = ExpenseCategory.FUEL,
                amount = 8200.0,
                expenseDate = now - (5 * dayMillis),
                title = "85L Diesel for 60HP Tractor",
                notes = "Field refuel for rotavator & tillage",
                receiptNumber = "IOC-85112"
            ),
            Expense(
                category = ExpenseCategory.LABOR,
                amount = 9000.0,
                expenseDate = now - (7 * dayMillis),
                title = "Harvester Operator & Helper Wages",
                notes = "10 days operating wage for Gurmukh & Jassi",
                receiptNumber = "WAG-01"
            ),
            Expense(
                category = ExpenseCategory.MAINTENANCE,
                amount = 4500.0,
                expenseDate = now - (11 * dayMillis),
                title = "Hydraulic Pipe & Filter Replacement",
                notes = "John Deere Authorized Service Center",
                receiptNumber = "INV-JD-4412"
            ),
            Expense(
                category = ExpenseCategory.LUBRICANTS_OIL,
                amount = 3200.0,
                expenseDate = now - (15 * dayMillis),
                title = "15W40 Engine Oil & Coolant Top-up",
                notes = "Castrol Agri Extra 7.5L can",
                receiptNumber = "CAS-3910"
            ),
            Expense(
                category = ExpenseCategory.SPARE_PARTS,
                amount = 2600.0,
                expenseDate = now - (4 * dayMillis),
                title = "Cultivator reversible shovels (set of 9)",
                notes = "Hardened carbon steel points",
                receiptNumber = "SHV-88"
            )
        )
        expenseDao.insertExpenses(expenses)
    }
}
