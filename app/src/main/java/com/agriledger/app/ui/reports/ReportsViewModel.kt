package com.agriledger.app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.FinancialReport
import com.agriledger.app.data.repository.AgriLedgerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ReportsViewModel(private val repository: AgriLedgerRepository) : ViewModel() {

    val financialReport: StateFlow<FinancialReport?> = repository.financialReport
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
