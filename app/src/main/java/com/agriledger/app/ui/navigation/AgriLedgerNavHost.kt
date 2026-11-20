package com.agriledger.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.agriledger.app.ui.customers.CustomerDetailScreen
import com.agriledger.app.ui.customers.CustomerViewModel
import com.agriledger.app.ui.customers.CustomersScreen
import com.agriledger.app.ui.dashboard.DashboardScreen
import com.agriledger.app.ui.dashboard.DashboardViewModel
import com.agriledger.app.ui.expenses.ExpenseViewModel
import com.agriledger.app.ui.expenses.ExpensesScreen
import com.agriledger.app.ui.reports.ReportsScreen
import com.agriledger.app.ui.reports.ReportsViewModel
import com.agriledger.app.ui.settings.SettingsScreen
import com.agriledger.app.ui.settings.SettingsViewModel

@Composable
fun AgriLedgerNavHost(
    navController: NavHostController,
    dashboardViewModel: DashboardViewModel,
    customerViewModel: CustomerViewModel,
    expenseViewModel: ExpenseViewModel,
    reportsViewModel: ReportsViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToCustomers = { navController.navigate(Screen.Customers.route) },
                onNavigateToCustomerDetail = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                },
                onNavigateToExpenses = { navController.navigate(Screen.Expenses.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) }
            )
        }

        composable(Screen.Customers.route) {
            CustomersScreen(
                viewModel = customerViewModel,
                onCustomerClick = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                }
            )
        }

        composable(
            route = Screen.CustomerDetail.route,
            arguments = listOf(
                navArgument("customerId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
            CustomerDetailScreen(
                customerId = customerId,
                viewModel = customerViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Expenses.route) {
            ExpensesScreen(
                viewModel = expenseViewModel
            )
        }

        composable(Screen.Reports.route) {
            ReportsScreen(
                viewModel = reportsViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel
            )
        }
    }
}
