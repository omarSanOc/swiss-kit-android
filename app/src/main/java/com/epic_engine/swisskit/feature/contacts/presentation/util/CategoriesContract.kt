package com.epic_engine.swisskit.feature.contacts.presentation.util

import com.epic_engine.swisskit.feature.contacts.domain.model.Category

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    // Add sheet
    val showAddSheet: Boolean = false,
    val addCategoryTitle: String = "",
    // Rename
    val renamingCategory: Category? = null,
    val renameTitle: String = "",
    // Delete confirmation
    val confirmDeleteCategory: Category? = null,
    // Toast
    val toastMessage: String? = null
)

sealed interface CategoriesEvent {
    data class NavigateToContacts(val categoryId: String, val categoryTitle: String) : CategoriesEvent
    data class ShowError(val message: String) : CategoriesEvent
    data object CategoryAdded : CategoriesEvent
    data object CategoryRenamed : CategoriesEvent
    data object CategoryDeleted : CategoriesEvent
}
