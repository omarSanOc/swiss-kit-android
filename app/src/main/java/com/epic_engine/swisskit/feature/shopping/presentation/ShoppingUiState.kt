package com.epic_engine.swisskit.feature.shopping.presentation

import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem

data class ShoppingUiState(
    val pendingItems: List<ShoppingItem> = emptyList(),
    val checkedItems: List<ShoppingItem> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val userMessage: String? = null
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
}
