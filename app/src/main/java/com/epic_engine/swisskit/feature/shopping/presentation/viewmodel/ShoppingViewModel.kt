package com.epic_engine.swisskit.feature.shopping.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem
import com.epic_engine.swisskit.feature.shopping.domain.usecase.AddShoppingItemUseCase
import com.epic_engine.swisskit.feature.shopping.domain.usecase.DeleteCheckedItemsUseCase
import com.epic_engine.swisskit.feature.shopping.domain.usecase.DeleteShoppingItemUseCase
import com.epic_engine.swisskit.feature.shopping.domain.usecase.DuplicateItemException
import com.epic_engine.swisskit.feature.shopping.domain.usecase.EditShoppingItemUseCase
import com.epic_engine.swisskit.feature.shopping.domain.usecase.ObserveShoppingItemsUseCase
import com.epic_engine.swisskit.feature.shopping.domain.usecase.ToggleShoppingItemUseCase
import com.epic_engine.swisskit.feature.shopping.domain.usecase.UncheckAllItemsUseCase
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.feature.shopping.presentation.utils.ShoppingEvent
import com.epic_engine.swisskit.feature.shopping.presentation.utils.ShoppingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    observeItems: ObserveShoppingItemsUseCase,
    private val addItem: AddShoppingItemUseCase,
    private val toggleItem: ToggleShoppingItemUseCase,
    private val deleteItem: DeleteShoppingItemUseCase,
    private val editItem: EditShoppingItemUseCase,
    private val uncheckAll: UncheckAllItemsUseCase,
    private val deleteChecked: DeleteCheckedItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingUiState())

    val uiState: StateFlow<ShoppingUiState> = combine(
        observeItems(),
        _uiState
    ) { items, state ->
        state.copy(
            pendingItems = items.filter { !it.isChecked },
            checkedItems = items.filter { it.isChecked }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShoppingUiState()
    )

    fun onEvent(event: ShoppingEvent) {
        when (event) {
            is ShoppingEvent.InputChanged -> _uiState.update { it.copy(inputText = event.text) }
            is ShoppingEvent.AddItem -> handleAdd(event.name)
            is ShoppingEvent.ToggleItem -> handleToggle(event.item)
            is ShoppingEvent.DeleteItem -> handleDelete(event.item)
            is ShoppingEvent.UncheckAll -> handleUncheckAll()
            is ShoppingEvent.DeleteChecked -> handleDeleteChecked()
            is ShoppingEvent.ShareList -> { /* Manejado en la UI con Intent */ }
            is ShoppingEvent.ClearMessage -> _uiState.update { it.copy(userMessage = null) }
            is ShoppingEvent.StartEdit -> _uiState.update {
                it.copy(editingItem = event.item, editText = event.item.name)
            }
            is ShoppingEvent.EditTextChanged -> _uiState.update { it.copy(editText = event.text) }
            is ShoppingEvent.ConfirmEdit -> handleConfirmEdit()
            is ShoppingEvent.CancelEdit -> _uiState.update {
                it.copy(editingItem = null, editText = "")
            }
            is ShoppingEvent.ShowDeleteCheckedDialog -> _uiState.update {
                it.copy(showDeleteCheckedDialog = true)
            }
            is ShoppingEvent.DismissDeleteDialog -> _uiState.update {
                it.copy(showDeleteCheckedDialog = false)
            }
            is ShoppingEvent.ClearDuplicateMessage -> _uiState.update {
                it.copy(duplicateMessage = null)
            }
            is ShoppingEvent.ShowDeleteItemDialog -> _uiState.update {
                it.copy(itemToDelete = event.item)
            }
            is ShoppingEvent.ConfirmDeleteItem -> handleConfirmDeleteItem()
            is ShoppingEvent.DismissDeleteItemDialog -> _uiState.update {
                it.copy(itemToDelete = null)
            }
        }
    }

    private fun handleAdd(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(inputText = "") }
            addItem(name)
                .onSuccess { /* Item observable via Flow */ }
                .onFailure { error ->
                    when (error) {
                        is DuplicateItemException -> {
                            _uiState.update { it.copy(duplicateMessage = UiText.StringRes(R.string.shopping_duplicate_message, error.itemName)) }
                            viewModelScope.launch {
                                delay(2_500)
                                _uiState.update { it.copy(duplicateMessage = null) }
                            }
                        }
                        is IllegalArgumentException -> _uiState.update {
                            it.copy(userMessage = UiText.StringRes(R.string.shopping_invalid_name))
                        }
                        else -> _uiState.update {
                            it.copy(userMessage = UiText.StringRes(R.string.shopping_error_add))
                        }
                    }
                }
        }
    }

    private fun handleToggle(item: ShoppingItem) {
        viewModelScope.launch {
            toggleItem(item).onFailure {
                _uiState.update { s -> s.copy(userMessage = UiText.StringRes(R.string.shopping_error_update)) }
            }
        }
    }

    private fun handleDelete(item: ShoppingItem) {
        viewModelScope.launch {
            deleteItem(item).onFailure {
                _uiState.update { s -> s.copy(userMessage = UiText.StringRes(R.string.shopping_error_delete)) }
            }
        }
    }

    private fun handleConfirmDeleteItem() {
        val item = _uiState.value.itemToDelete ?: return
        _uiState.update { it.copy(itemToDelete = null) }
        viewModelScope.launch {
            deleteItem(item).onFailure {
                _uiState.update { s -> s.copy(userMessage = UiText.StringRes(R.string.shopping_error_delete)) }
            }
        }
    }

    private fun handleConfirmEdit() {
        val state = _uiState.value
        val item = state.editingItem ?: return
        val newName = state.editText.trim()
        _uiState.update { it.copy(editingItem = null, editText = "") }
        viewModelScope.launch {
            editItem(item, newName).onFailure {
                _uiState.update { s -> s.copy(userMessage = UiText.StringRes(R.string.shopping_error_edit)) }
            }
        }
    }

    private fun handleUncheckAll() {
        viewModelScope.launch {
            uncheckAll().onFailure {
                _uiState.update { s -> s.copy(userMessage = UiText.StringRes(R.string.shopping_error_uncheck)) }
            }
        }
    }

    private fun handleDeleteChecked() {
        viewModelScope.launch {
            deleteChecked().onFailure {
                _uiState.update { s -> s.copy(userMessage = UiText.StringRes(R.string.shopping_error_delete_checked)) }
            }
        }
    }

}
