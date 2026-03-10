package com.epic_engine.swisskit.feature.finance.presentation

import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceSortOrder
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType

data class FinanceUiState(
    val allItems: List<Finance> = emptyList(),
    val filteredItems: List<Finance> = emptyList(),
    val searchQuery: String = "",
    val selectedCategories: Set<String> = emptySet(),
    val typeFilter: FinanceType? = null,
    val sortOrder: FinanceSortOrder = FinanceSortOrder.DESCENDING,
    val selectedIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val showFilterSheet: Boolean = false
) {
    val totalIncome: Double get() = allItems.filter { it.type == FinanceType.INCOME }.sumOf { it.amount }
    val totalExpenses: Double get() = allItems.filter { it.type == FinanceType.EXPENSE }.sumOf { it.amount }
    val netBalance: Double get() = totalIncome - totalExpenses
    val hasSelection: Boolean get() = selectedIds.isNotEmpty()
    val hasItems: Boolean get() = allItems.isNotEmpty()
    val isFiltered: Boolean get() = searchQuery.isNotBlank() || selectedCategories.isNotEmpty() || typeFilter != null
}

sealed class FinanceEvent {
    data class SearchChanged(val query: String) : FinanceEvent()
    data class ToggleCategoryFilter(val category: String) : FinanceEvent()
    data class SetTypeFilter(val type: FinanceType?) : FinanceEvent()
    data class ToggleSortOrder(val order: FinanceSortOrder) : FinanceEvent()
    data class ToggleSelection(val id: String) : FinanceEvent()
    data object SelectAll : FinanceEvent()
    data object ClearSelection : FinanceEvent()
    data object DeleteSelected : FinanceEvent()
    data class DeleteItem(val item: Finance) : FinanceEvent()
    data object ToggleFilterSheet : FinanceEvent()
    data object ClearFilters : FinanceEvent()
    data object ClearMessage : FinanceEvent()
    data object ExportPdf : FinanceEvent()
    data object BackupJson : FinanceEvent()
    data class RestoreJson(val content: String) : FinanceEvent()
}
