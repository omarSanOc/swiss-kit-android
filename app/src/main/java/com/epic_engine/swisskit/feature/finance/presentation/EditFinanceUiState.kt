package com.epic_engine.swisskit.feature.finance.presentation

import com.epic_engine.swisskit.feature.finance.domain.model.FinanceCategoryData
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType

data class EditFinanceUiState(
    val id: String? = null,
    val title: String = "",
    val amountInput: String = "",
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val category: String = "",
    val type: FinanceType = FinanceType.EXPENSE,
    val isSaving: Boolean = false,
    val validationError: String? = null,
    val savedSuccessfully: Boolean = false
) {
    val isEditing: Boolean get() = id != null
    val availableCategories: List<String> get() = FinanceCategoryData.categoriesFor(type)
    val canSave: Boolean get() = title.isNotBlank() && amountInput.toDoubleOrNull() != null
                                  && (amountInput.toDoubleOrNull() ?: 0.0) > 0 && category.isNotBlank()
}

sealed class EditFinanceEvent {
    data class TitleChanged(val value: String) : EditFinanceEvent()
    data class AmountChanged(val value: String) : EditFinanceEvent()
    data class DateChanged(val epochMillis: Long) : EditFinanceEvent()
    data class NotesChanged(val value: String) : EditFinanceEvent()
    data class CategoryChanged(val value: String) : EditFinanceEvent()
    data class TypeChanged(val type: FinanceType) : EditFinanceEvent()
    data object Save : EditFinanceEvent()
    data object ClearError : EditFinanceEvent()
}
