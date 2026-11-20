package com.agriledger.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.DashboardStats
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.OutstandingCustomer
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.data.repository.AgriLedgerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: AgriLedgerRepository) : ViewModel() {

    val stats: StateFlow<DashboardStats> = repository.dashboardStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    val recentJobs: StateFlow<List<ServiceJob>> = repository.recentJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentPayments: StateFlow<List<Payment>> = repository.recentPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentExpenses: StateFlow<List<Expense>> = repository.recentExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outstandingCustomers: StateFlow<List<OutstandingCustomer>> = repository.outstandingCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCustomer(customer: Customer, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertCustomer(customer)
            onDone()
        }
    }

    fun recordPayment(payment: Payment, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.recordPayment(payment)
            onDone()
        }
    }

    fun addServiceJob(job: ServiceJob, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.createServiceJob(job)
            onDone()
        }
    }

    fun addExpense(expense: Expense, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertExpense(expense)
            onDone()
        }
    }
}
