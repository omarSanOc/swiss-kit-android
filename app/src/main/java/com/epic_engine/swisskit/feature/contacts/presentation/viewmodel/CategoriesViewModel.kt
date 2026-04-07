package com.epic_engine.swisskit.feature.contacts.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.feature.contacts.domain.model.Category
import com.epic_engine.swisskit.feature.contacts.domain.usecase.AddCategoryUseCase
import com.epic_engine.swisskit.feature.contacts.domain.usecase.DeleteCategoryUseCase
import com.epic_engine.swisskit.feature.contacts.domain.usecase.GetCategoriesUseCase
import com.epic_engine.swisskit.feature.contacts.domain.usecase.RenameCategoryUseCase
import com.epic_engine.swisskit.feature.contacts.presentation.util.CategoriesEvent
import com.epic_engine.swisskit.feature.contacts.presentation.util.CategoriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategories: GetCategoriesUseCase,
    private val addCategory: AddCategoryUseCase,
    private val renameCategory: RenameCategoryUseCase,
    private val deleteCategory: DeleteCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CategoriesEvent>()
    val events: SharedFlow<CategoriesEvent> = _events.asSharedFlow()

    init {
        observeCategories()
    }

    private fun observeCategories() {
        getCategories()
            .onEach { categories ->
                _uiState.update { it.copy(categories = categories, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) = _uiState.update { it.copy(searchQuery = query) }

    fun onSelectCategory(categoryId: String, title: String) {
        viewModelScope.launch {
            _events.emit(CategoriesEvent.NavigateToContacts(categoryId, title))
        }
    }

    // Add sheet
    fun onShowAddSheet() = _uiState.update { it.copy(showAddSheet = true, addCategoryTitle = "") }
    fun onDismissAddSheet() = _uiState.update { it.copy(showAddSheet = false) }
    fun onAddCategoryTitleChange(title: String) = _uiState.update { it.copy(addCategoryTitle = title) }

    fun onConfirmAdd() {
        val title = _uiState.value.addCategoryTitle
        viewModelScope.launch {
            runCatching { addCategory(title) }
                .onSuccess {
                    _uiState.update { it.copy(showAddSheet = false, toastMessage = UiText.StringRes(R.string.category_toast_saved)) }
                    _events.emit(CategoriesEvent.CategoryAdded)
                }
                .onFailure { _events.emit(CategoriesEvent.ShowError(UiText.StringRes(R.string.common_error))) }
        }
    }

    // Rename
    fun onStartRename(category: Category) =
        _uiState.update { it.copy(renamingCategory = category, renameTitle = category.title) }

    fun onRenameTitleChange(title: String) = _uiState.update { it.copy(renameTitle = title) }
    fun onDismissRename() = _uiState.update { it.copy(renamingCategory = null) }

    fun onConfirmRename() {
        val state = _uiState.value
        val cat = state.renamingCategory ?: return
        viewModelScope.launch {
            runCatching { renameCategory(cat.id, state.renameTitle) }
                .onSuccess {
                    _uiState.update { it.copy(renamingCategory = null, toastMessage = UiText.StringRes(R.string.category_toast_updated)) }
                    _events.emit(CategoriesEvent.CategoryRenamed)
                }
                .onFailure { _events.emit(CategoriesEvent.ShowError(UiText.StringRes(R.string.common_error))) }
        }
    }

    // Delete (with confirmation)
    fun onRequestDeleteCategory(category: Category) =
        _uiState.update { it.copy(confirmDeleteCategory = category) }

    fun onDismissDeleteConfirm() =
        _uiState.update { it.copy(confirmDeleteCategory = null) }

    fun onConfirmDeleteCategory() {
        val category = _uiState.value.confirmDeleteCategory ?: return
        _uiState.update { it.copy(confirmDeleteCategory = null) }
        viewModelScope.launch {
            runCatching { deleteCategory(category.id) }
                .onSuccess {
                    _uiState.update { it.copy(toastMessage = UiText.StringRes(R.string.category_toast_deleted)) }
                    _events.emit(CategoriesEvent.CategoryDeleted)
                }
                .onFailure { _events.emit(CategoriesEvent.ShowError(UiText.StringRes(R.string.common_error))) }
        }
    }

    fun onDismissToast() = _uiState.update { it.copy(toastMessage = null) }
}
