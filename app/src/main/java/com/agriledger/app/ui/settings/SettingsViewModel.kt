package com.agriledger.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agriledger.app.data.database.AgriLedgerDatabase
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.repository.AgriLedgerRepository
import com.agriledger.app.utils.SampleDataGenerator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: AgriLedgerRepository,
    private val database: AgriLedgerDatabase
) : ViewModel() {

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveBusinessProfile(profile: BusinessProfile, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveBusinessProfile(profile)
            onDone()
        }
    }

    fun seedSampleData(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            SampleDataGenerator.seedDatabase(database)
            onDone()
        }
    }

    fun clearAllData(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.clearAllData()
            onDone()
        }
    }
}
