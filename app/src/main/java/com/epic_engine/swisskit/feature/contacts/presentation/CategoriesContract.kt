package com.epic_engine.swisskit.feature.contacts.presentation

import com.epic_engine.swisskit.feature.contacts.domain.model.Category

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val showAddSheet: Boolean = false,
    val addCategoryTitle: String = "",
    val renamingCategory: Category? = null,
    val renameTitle: String = ""
)

sealed interface CategoriesEvent {
    data class NavigateToContacts(val categoryId: String, val categoryTitle: String) : CategoriesEvent
    data class ShowError(val message: String) : CategoriesEvent
    data object CategoryAdded : CategoriesEvent
    data object CategoryRenamed : CategoriesEvent
    data object CategoryDeleted : CategoriesEvent
}
