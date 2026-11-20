package com.agriledger.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    data object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Customers : Screen("customers", "Customers", Icons.Filled.People, Icons.Outlined.People)
    data object Expenses : Screen("expenses", "Expenses", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)
    data object Reports : Screen("reports", "Reports", Icons.Filled.Analytics, Icons.Outlined.Analytics)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)

    // Sub-screens
    data object CustomerDetail : Screen("customer_detail/{customerId}", "Customer Detail") {
        fun createRoute(customerId: Long): String = "customer_detail/$customerId"
    }
    data object ServiceJobs : Screen("service_jobs", "Service Invoices")
    data object Payments : Screen("payments", "Payments History")

    companion object {
        val bottomNavItems = listOf(
            Dashboard,
            Customers,
            Expenses,
            Reports,
            Settings
        )
    }
}
