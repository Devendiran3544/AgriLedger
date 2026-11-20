package com.agriledger.app.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.CustomerWithSummary
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.data.repository.AgriLedgerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CustomerDetailUiState {
    data object Loading : CustomerDetailUiState
    data class Success(
        val customer: Customer,
        val jobs: List<ServiceJob>,
        val payments: List<Payment>
    ) : CustomerDetailUiState
    data class Error(val message: String) : CustomerDetailUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class CustomerViewModel(private val repository: AgriLedgerRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val customersWithSummary: StateFlow<List<CustomerWithSummary>> = combine(
        repository.customersWithSummary,
        _searchQuery,
        _selectedFilter
    ) { list, query, filter ->
        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.customer.name.contains(query, ignoreCase = true) ||
                    item.customer.phone.contains(query, ignoreCase = true) ||
                    item.customer.village.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Has Balance" -> item.totalPending > 0.0
                "Cleared" -> item.totalPending == 0.0 && item.totalBilled > 0.0
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Customer Detail State
    private val _selectedCustomerId = MutableStateFlow<Long?>(null)

    val customerDetailState: StateFlow<CustomerDetailUiState> = _selectedCustomerId
        .flatMapLatest { id ->
            if (id == null || id <= 0L) {
                flowOf(CustomerDetailUiState.Error("Invalid or missing customer ID"))
            } else {
                combine(
                    repository.getCustomerByIdFlow(id),
                    repository.getJobsForCustomer(id),
                    repository.getPaymentsForCustomer(id)
                ) { customer, jobs, payments ->
                    if (customer == null) {
                        CustomerDetailUiState.Error("Customer not found or was removed from ledger.")
                    } else {
                        CustomerDetailUiState.Success(
                            customer = customer,
                            jobs = jobs,
                            payments = payments
                        )
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CustomerDetailUiState.Loading
        )

    fun loadCustomer(customerId: Long) {
        _selectedCustomerId.value = customerId
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChange(filter: String) {
        _selectedFilter.value = filter
    }

    fun addCustomer(customer: Customer, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertCustomer(customer)
            onDone()
        }
    }

    fun updateCustomer(customer: Customer, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            onDone()
        }
    }

    fun deleteCustomer(customer: Customer, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
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

    // Customer Detail Screen Helpers (backward-compatible)
    fun getCustomerById(customerId: Long): StateFlow<Customer?> {
        return repository.getCustomerByIdFlow(customerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun getJobsForCustomer(customerId: Long): StateFlow<List<ServiceJob>> {
        return repository.getJobsForCustomer(customerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getPaymentsForCustomer(customerId: Long): StateFlow<List<Payment>> {
        return repository.getPaymentsForCustomer(customerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}
