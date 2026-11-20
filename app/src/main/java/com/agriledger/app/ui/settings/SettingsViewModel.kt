package com.agriledger.app.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agriledger.app.data.database.AgriLedgerDatabase
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.repository.AgriLedgerRepository
import com.agriledger.app.utils.BackupManager
import com.agriledger.app.utils.SampleDataGenerator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: AgriLedgerRepository,
    val database: AgriLedgerDatabase
) : ViewModel() {

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveBusinessProfile(profile: BusinessProfile, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveBusinessProfile(profile)
            onDone()
        }
    }

    fun exportBackup(context: Context, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            BackupManager.exportAndShareBackup(context, database)
            onDone()
        }
    }

    fun restoreBackup(jsonString: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = BackupManager.restoreDatabaseFromJson(database, jsonString)
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.localizedMessage ?: "Unknown error")
            }
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
