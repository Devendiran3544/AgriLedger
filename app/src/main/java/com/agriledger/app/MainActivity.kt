package com.agriledger.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.agriledger.app.ui.customers.CustomerViewModel
import com.agriledger.app.ui.dashboard.DashboardViewModel
import com.agriledger.app.ui.expenses.ExpenseViewModel
import com.agriledger.app.ui.navigation.AgriLedgerBottomBar
import com.agriledger.app.ui.navigation.AgriLedgerNavHost
import com.agriledger.app.ui.reports.ReportsViewModel
import com.agriledger.app.ui.settings.SettingsViewModel
import com.agriledger.app.ui.theme.AgriLedgerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AgriLedgerApplication
        val repository = app.repository
        val database = app.database

        setContent {
            AgriLedgerTheme {
                val navController = rememberNavController()

                // Create ViewModels via simple factories
                val dashboardViewModel: DashboardViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return DashboardViewModel(repository) as T
                        }
                    }
                )

                val customerViewModel: CustomerViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return CustomerViewModel(repository) as T
                        }
                    }
                )

                val expenseViewModel: ExpenseViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ExpenseViewModel(repository) as T
                        }
                    }
                )

                val reportsViewModel: ReportsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ReportsViewModel(repository) as T
                        }
                    }
                )

                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return SettingsViewModel(repository, database) as T
                        }
                    }
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AgriLedgerBottomBar(navController = navController)
                    }
                ) { innerPadding ->
                    AgriLedgerNavHost(
                        navController = navController,
                        dashboardViewModel = dashboardViewModel,
                        customerViewModel = customerViewModel,
                        expenseViewModel = expenseViewModel,
                        reportsViewModel = reportsViewModel,
                        settingsViewModel = settingsViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
