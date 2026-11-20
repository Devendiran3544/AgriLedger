package com.agriledger.app.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Expense
import com.agriledger.app.data.model.ExpenseCategory
import com.agriledger.app.data.repository.AgriLedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: AgriLedgerRepository) : ViewModel() {

    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val expenses: StateFlow<List<Expense>> = combine(
        repository.allExpenses,
        _selectedCategoryFilter,
        _searchQuery
    ) { list, categoryFilter, query ->
        list.filter { expense ->
            val matchesCategory = if (categoryFilter == "All") {
                true
            } else {
                expense.category.displayName == categoryFilter
            }

            val matchesQuery = query.isBlank() ||
                    expense.title.contains(query, ignoreCase = true) ||
                    expense.notes.contains(query, ignoreCase = true) ||
                    expense.receiptNumber.contains(query, ignoreCase = true)

            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpenses: StateFlow<Double> = repository.allExpenses
        .combine(_selectedCategoryFilter) { list, filter ->
            if (filter == "All") {
                list.sumOf { it.amount }
            } else {
                list.filter { it.category.displayName == filter }.sumOf { it.amount }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun onCategoryFilterChange(filter: String) {
        _selectedCategoryFilter.value = filter
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addExpense(expense: Expense, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertExpense(expense)
            onDone()
        }
    }

    fun deleteExpense(expense: Expense, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            onDone()
        }
    }
}
