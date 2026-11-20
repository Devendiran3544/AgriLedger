package com.agriledger.app.ui.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.Payment
import com.agriledger.app.data.model.PaymentMode
import com.agriledger.app.data.repository.AgriLedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaymentViewModel(private val repository: AgriLedgerRepository) : ViewModel() {

    private val _selectedModeFilter = MutableStateFlow("All")
    val selectedModeFilter: StateFlow<String> = _selectedModeFilter.asStateFlow()

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<Payment>> = combine(
        repository.allPayments,
        _selectedModeFilter
    ) { list, filter ->
        if (filter == "All") {
            list
        } else {
            list.filter { it.paymentMode.displayName == filter }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onModeFilterChange(filter: String) {
        _selectedModeFilter.value = filter
    }

    fun recordPayment(payment: Payment, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.recordPayment(payment)
            onDone()
        }
    }

    fun deletePayment(payment: Payment, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deletePayment(payment)
            onDone()
        }
    }
}
