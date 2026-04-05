package com.epic_engine.swisskit.feature.finance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceSortOrder
import com.epic_engine.swisskit.feature.finance.domain.usecase.BackupFinanceUseCase
import com.epic_engine.swisskit.feature.finance.domain.usecase.DeleteFinanceBatchUseCase
import com.epic_engine.swisskit.feature.finance.domain.usecase.DeleteFinanceUseCase
import com.epic_engine.swisskit.feature.finance.domain.usecase.ExportFinanceToPdfUseCase
import com.epic_engine.swisskit.feature.finance.domain.usecase.FilterFinanceUseCase
import com.epic_engine.swisskit.feature.finance.domain.usecase.FinanceCategorySelectionEngine
import com.epic_engine.swisskit.feature.finance.domain.usecase.GetDistinctCategoriesUseCase
import com.epic_engine.swisskit.feature.finance.domain.usecase.ObserveFinanceUseCase
import com.epic_engine.swisskit.feature.finance.domain.usecase.RestoreFinanceUseCase
import com.epic_engine.swisskit.feature.finance.presentation.utils.FinanceEvent
import com.epic_engine.swisskit.feature.finance.presentation.utils.FinanceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val observeFinance: ObserveFinanceUseCase,
    private val deleteItem: DeleteFinanceUseCase,
    private val deleteBatch: DeleteFinanceBatchUseCase,
    private val filterItems: FilterFinanceUseCase,
    private val getDistinctCategories: GetDistinctCategoriesUseCase,
    private val exportToPdf: ExportFinanceToPdfUseCase,
    private val backup: BackupFinanceUseCase,
    private val restore: RestoreFinanceUseCase
) : ViewModel() {

    private val selectionEngine = FinanceCategorySelectionEngine()

    private val _uiState = MutableStateFlow(FinanceUiState())

    private val _pdfBytes = MutableSharedFlow<ByteArray>()
    val pdfBytes: SharedFlow<ByteArray> = _pdfBytes

    private val _backupJson = MutableSharedFlow<String>()
    val backupJson: SharedFlow<String> = _backupJson

    val uiState: StateFlow<FinanceUiState> = combine(
        observeFinance(FinanceSortOrder.DESCENDING),
        _uiState,
        getDistinctCategories()
    ) { items, state, categories ->
        val filtered = filterItems(items, state.searchQuery, state.selectedCategories)
        val sorted = if (state.sortOrder == FinanceSortOrder.ASCENDING) filtered.sortedBy { it.date }
                     else filtered.sortedByDescending { it.date }
        val filteredIds = sorted.map { it.id }.toSet()
        val reconciledIds = state.selectedIds.intersect(filteredIds)
        state.copy(allItems = items, filteredItems = sorted, availableCategories = categories, selectedIds = reconciledIds)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FinanceUiState())

    fun onEvent(event: FinanceEvent) {
        when (event) {
            is FinanceEvent.SearchChanged -> _uiState.update { it.copy(searchQuery = event.query) }
            is FinanceEvent.ToggleCategoryFilter -> toggleCategoryFilter(event.category)
            is FinanceEvent.ToggleSortOrder -> _uiState.update { it.copy(sortOrder = event.order) }
            is FinanceEvent.ToggleSelection -> toggleSelection(event.id)
            is FinanceEvent.SelectAll -> {
                val ids = uiState.value.filteredItems.map { it.id }.toSet()
                _uiState.update { it.copy(selectedIds = ids, isSelectionMode = true) }
            }
            is FinanceEvent.ClearSelection -> _uiState.update { it.copy(selectedIds = emptySet(), isSelectionMode = false) }
            is FinanceEvent.DeleteSelected -> deleteSelected()
            is FinanceEvent.DeleteItem -> handleDelete(event.item)
            is FinanceEvent.ToggleFilterSheet -> _uiState.update { it.copy(showFilterSheet = !it.showFilterSheet) }
            is FinanceEvent.ClearFilters -> _uiState.update { it.copy(searchQuery = "", selectedCategories = setOf(FinanceCategorySelectionEngine.LABEL_ALL)) }
            is FinanceEvent.ClearMessage -> _uiState.update { it.copy(userMessage = null) }
            is FinanceEvent.ExportPdf -> handleExportPdf()
            is FinanceEvent.BackupJson -> handleBackup()
            is FinanceEvent.RestoreJson -> handleRestore(event.content)
            is FinanceEvent.DeselectAll -> _uiState.update { it.copy(selectedIds = emptySet()) }
            is FinanceEvent.EnterSelectionMode -> _uiState.update { it.copy(isSelectionMode = true, selectedIds = emptySet()) }
        }
    }

    private fun toggleCategoryFilter(category: String) {
        _uiState.update { state ->
            state.copy(selectedCategories = selectionEngine.toggleSelection(category, state.selectedCategories))
        }
    }

    private fun toggleSelection(id: String) {
        _uiState.update { state ->
            val updated = state.selectedIds.toMutableSet()
            if (id in updated) updated.remove(id) else updated.add(id)
            state.copy(selectedIds = updated, isSelectionMode = updated.isNotEmpty())
        }
    }

    private fun deleteSelected() {
        val ids = _uiState.value.selectedIds
        if (ids.isEmpty()) return
        viewModelScope.launch {
            deleteBatch(ids)
                .onSuccess { _uiState.update { it.copy(selectedIds = emptySet(), isSelectionMode = false, userMessage = "${ids.size} transacciones eliminadas") } }
                .onFailure { _uiState.update { s -> s.copy(userMessage = "Error al eliminar transacciones") } }
        }
    }

    private fun handleDelete(item: Finance) {
        viewModelScope.launch {
            deleteItem(item).onFailure {
                _uiState.update { s -> s.copy(userMessage = "Error al eliminar transacción") }
            }
        }
    }

    private fun handleExportPdf() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            exportToPdf()
                .onSuccess { bytes ->
                    _uiState.update { it.copy(isLoading = false) }
                    _pdfBytes.emit(bytes)
                }
                .onFailure {
                    _uiState.update { s -> s.copy(isLoading = false, userMessage = "Error al generar PDF") }
                }
        }
    }

    private fun handleBackup() {
        viewModelScope.launch {
            backup()
                .onSuccess { json -> _backupJson.emit(json) }
                .onFailure { _uiState.update { s -> s.copy(userMessage = "Error al generar backup") } }
        }
    }

    private fun handleRestore(content: String) {
        viewModelScope.launch {
            restore(content)
                .onSuccess { count -> _uiState.update { it.copy(userMessage = "$count transacciones restauradas") } }
                .onFailure { _uiState.update { s -> s.copy(userMessage = "Error al restaurar backup: formato inválido") } }
        }
    }
}
