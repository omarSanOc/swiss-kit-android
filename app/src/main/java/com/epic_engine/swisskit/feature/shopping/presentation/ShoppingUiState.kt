package com.epic_engine.swisskit.feature.shopping.presentation

import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem

data class ShoppingUiState(
    val pendingItems: List<ShoppingItem> = emptyList(),
    val checkedItems: List<ShoppingItem> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val editingItem: ShoppingItem? = null,
    val editText: String = "",
    val showDeleteCheckedDialog: Boolean = false,
    val itemToDelete: ShoppingItem? = null,
    val duplicateMessage: String? = null
) {
    val hasCheckedItems: Boolean get() = checkedItems.isNotEmpty()
    val hasAnyItems: Boolean get() = pendingItems.isNotEmpty() || checkedItems.isNotEmpty()
    val allItems: List<ShoppingItem> get() = pendingItems + checkedItems
}

sealed class ShoppingEvent {
    data class AddItem(val name: String) : ShoppingEvent()
    data class ToggleItem(val item: ShoppingItem) : ShoppingEvent()
    data class DeleteItem(val item: ShoppingItem) : ShoppingEvent()
    data object UncheckAll : ShoppingEvent()
    data object DeleteChecked : ShoppingEvent()
    data object ShareList : ShoppingEvent()
    data object ClearMessage : ShoppingEvent()
    data class InputChanged(val text: String) : ShoppingEvent()
    data class StartEdit(val item: ShoppingItem) : ShoppingEvent()
    data class EditTextChanged(val text: String) : ShoppingEvent()
    data object ConfirmEdit : ShoppingEvent()
    data object CancelEdit : ShoppingEvent()
    data object ShowDeleteCheckedDialog : ShoppingEvent()
    data object DismissDeleteDialog : ShoppingEvent()
    data object ClearDuplicateMessage : ShoppingEvent()
    data class ShowDeleteItemDialog(val item: ShoppingItem) : ShoppingEvent()
    data object ConfirmDeleteItem : ShoppingEvent()
    data object DismissDeleteItemDialog : ShoppingEvent()
}
