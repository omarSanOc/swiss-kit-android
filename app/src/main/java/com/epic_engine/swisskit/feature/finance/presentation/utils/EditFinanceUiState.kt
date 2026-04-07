package com.epic_engine.swisskit.feature.finance.presentation.utils

import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType

data class EditFinanceUiState(
    val id: String? = null,
    val title: String = "",
    val amountInput: String = "",
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val category: String = "General",
    val type: FinanceType = FinanceType.EXPENSE,
    val isSaving: Boolean = false,
    val validationError: UiText? = null,
    val savedSuccessfully: Boolean = false,
    val availableCategories: List<String> = emptyList()
) {
    val isEditing: Boolean get() = id != null
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
