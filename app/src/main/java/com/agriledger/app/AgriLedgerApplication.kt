package com.agriledger.app

import android.app.Application
import com.agriledger.app.data.database.AgriLedgerDatabase
import com.agriledger.app.data.repository.AgriLedgerRepository

class AgriLedgerApplication : Application() {

    lateinit var database: AgriLedgerDatabase
        private set

    lateinit var repository: AgriLedgerRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AgriLedgerDatabase.getInstance(this)
        repository = AgriLedgerRepository(database)
    }
}
