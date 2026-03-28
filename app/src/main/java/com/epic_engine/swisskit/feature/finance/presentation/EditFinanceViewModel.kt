package com.epic_engine.swisskit.feature.finance.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.domain.usecase.AddFinanceUseCase
import com.epic_engine.swisskit.feature.finance.domain.usecase.GetDistinctCategoriesUseCase
import com.epic_engine.swisskit.feature.finance.domain.usecase.GetFinanceByIdUseCase
import com.epic_engine.swisskit.feature.finance.domain.usecase.UpdateFinanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditFinanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFinanceById: GetFinanceByIdUseCase,
    private val addFinance: AddFinanceUseCase,
    private val updateFinance: UpdateFinanceUseCase,
    private val getDistinctCategories: GetDistinctCategoriesUseCase
) : ViewModel() {

    private val financeId: String? = savedStateHandle.get<String>("financeId")?.takeIf { it != "new" }

    private val _uiState = MutableStateFlow(EditFinanceUiState(id = financeId))
    val uiState: StateFlow<EditFinanceUiState> = _uiState.asStateFlow()

    init {
        loadFinance()
        viewModelScope.launch {
            getDistinctCategories().collect { categories ->
                val withDefault = if ("General" in categories) categories else listOf("General") + categories
                _uiState.update { it.copy(availableCategories = withDefault) }
            }
        }
    }

    private fun loadFinance() {
        viewModelScope.launch {
            val itemId = financeId ?: return@launch
            val item = getFinanceById(itemId)
            if (item != null) {
                loadForEdit(item)
            } else {
                _uiState.update { it.copy(validationError = "Transacción no encontrada") }
            }
        }
    }

    fun loadForEdit(item: Finance) {
        _uiState.update { currentState ->
            currentState.copy(
                id = item.id,
                title = item.title,
                amountInput = item.amount.toString(),
                date = item.date,
                notes = item.notes ?: "",
                category = item.category,
                type = item.type
            )
        }
    }

    fun onEvent(event: EditFinanceEvent) {
        when (event) {
            is EditFinanceEvent.TitleChanged -> _uiState.update { it.copy(title = event.value, validationError = null) }
            is EditFinanceEvent.AmountChanged -> _uiState.update { it.copy(amountInput = event.value, validationError = null) }
            is EditFinanceEvent.DateChanged -> _uiState.update { it.copy(date = event.epochMillis) }
            is EditFinanceEvent.NotesChanged -> _uiState.update { it.copy(notes = event.value) }
            is EditFinanceEvent.CategoryChanged -> _uiState.update { it.copy(category = event.value) }
            is EditFinanceEvent.TypeChanged -> {
                _uiState.update { it.copy(type = event.type) }
            }
            is EditFinanceEvent.Save -> handleSave()
            is EditFinanceEvent.ClearError -> _uiState.update { it.copy(validationError = null) }
        }
    }

    private fun handleSave() {
        val state = _uiState.value
        val amount = state.amountInput.toDoubleOrNull()
            ?: run { _uiState.update { it.copy(validationError = "Monto inválido") }; return }

        val finance = Finance(
            id = state.id ?: UUID.randomUUID().toString(),
            title = state.title.trim(),
            amount = amount,
            date = state.date,
            notes = state.notes.trim().ifBlank { null },
            category = state.category,
            type = state.type
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = if (state.isEditing) updateFinance(finance) else addFinance(finance)
            result
                .onSuccess { _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) } }
                .onFailure { e -> _uiState.update { it.copy(isSaving = false, validationError = e.message) } }
        }
    }
}
