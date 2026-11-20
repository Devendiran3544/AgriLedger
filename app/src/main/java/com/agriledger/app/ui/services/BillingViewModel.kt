package com.agriledger.app.ui.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.data.model.ServiceJob
import com.agriledger.app.data.repository.AgriLedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BillingViewModel(private val repository: AgriLedgerRepository) : ViewModel() {

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJobs: StateFlow<List<ServiceJob>> = combine(
        repository.allServiceJobs,
        _statusFilter,
        _searchQuery
    ) { jobs, filter, query ->
        jobs.filter { job ->
            val matchesFilter = when (filter) {
                "Unpaid" -> job.status == PaymentStatus.UNPAID
                "Partial" -> job.status == PaymentStatus.PARTIAL
                "Paid" -> job.status == PaymentStatus.PAID
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                    job.invoiceNumber.contains(query, ignoreCase = true) ||
                    job.serviceType.displayName.contains(query, ignoreCase = true) ||
                    job.notes.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onFilterChange(filter: String) {
        _statusFilter.value = filter
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addServiceJob(job: ServiceJob, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.createServiceJob(job)
            onDone()
        }
    }

    fun updateServiceJob(job: ServiceJob, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateServiceJob(job)
            onDone()
        }
    }

    fun deleteServiceJob(job: ServiceJob, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteServiceJob(job)
            onDone()
        }
    }
}
