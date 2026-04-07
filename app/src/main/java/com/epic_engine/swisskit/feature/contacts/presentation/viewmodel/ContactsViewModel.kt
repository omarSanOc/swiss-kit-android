package com.epic_engine.swisskit.feature.contacts.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.domain.model.ContactAction
import com.epic_engine.swisskit.feature.contacts.domain.usecase.AddContactUseCase
import com.epic_engine.swisskit.feature.contacts.domain.usecase.DeleteAllContactsUseCase
import com.epic_engine.swisskit.feature.contacts.domain.usecase.DeleteContactUseCase
import com.epic_engine.swisskit.feature.contacts.domain.usecase.GetContactsUseCase
import com.epic_engine.swisskit.feature.contacts.domain.usecase.SearchContactsUseCase
import com.epic_engine.swisskit.feature.contacts.domain.usecase.UpdateContactUseCase
import com.epic_engine.swisskit.feature.contacts.domain.util.PhoneNumberNormalizer
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.feature.contacts.presentation.util.ContactsEvent
import com.epic_engine.swisskit.feature.contacts.presentation.util.ContactsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getContacts: GetContactsUseCase,
    private val searchContacts: SearchContactsUseCase,
    private val addContact: AddContactUseCase,
    private val updateContact: UpdateContactUseCase,
    private val deleteContact: DeleteContactUseCase,
    private val deleteAllContacts: DeleteAllContactsUseCase
) : ViewModel() {

    val categoryId: String = checkNotNull(savedStateHandle["categoryId"])

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ContactsEvent>()
    val events: SharedFlow<ContactsEvent> = _events.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        observeContacts()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeContacts() {
        _searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isBlank()) getContacts(categoryId)
                else searchContacts(categoryId, query)
            }
            .onEach { contacts ->
                _uiState.update { it.copy(contacts = contacts, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    // Action sheet
    fun onShowActionSheet(contact: Contact) =
        _uiState.update { it.copy(actionSheetContact = contact) }

    fun onDismissActionSheet() =
        _uiState.update { it.copy(actionSheetContact = null) }

    fun onContactAction(contact: Contact, action: ContactAction) {
        val url = when (action) {
            ContactAction.CALL -> PhoneNumberNormalizer.toCallUrl(contact.phone)
            ContactAction.WHATSAPP -> PhoneNumberNormalizer.toWhatsAppUrl(contact.phone)
        }
        val toastMsg = when (action) {
            ContactAction.CALL -> UiText.StringRes(R.string.contacts_toast_calling)
            ContactAction.WHATSAPP -> UiText.StringRes(R.string.contacts_toast_whatsapp)
        }
        _uiState.update { it.copy(actionSheetContact = null, toastMessage = toastMsg) }
        viewModelScope.launch {
            _events.emit(ContactsEvent.LaunchContactAction(action, url))
        }
    }

    // Add/Edit sheet
    fun onShowAddSheet() =
        _uiState.update {
            it.copy(
                showAddSheet = true,
                editingContact = null,
                nameDraft = "",
                phoneDraft = "",
                phoneError = null
            )
        }

    fun onEditContact(contact: Contact) =
        _uiState.update {
            it.copy(
                showAddSheet = true,
                editingContact = contact,
                nameDraft = contact.name,
                phoneDraft = contact.phone,
                phoneError = null
            )
        }

    fun onDismissSheet() = _uiState.update { it.copy(showAddSheet = false, editingContact = null) }
    fun onNameChange(v: String) = _uiState.update { it.copy(nameDraft = v) }
    fun onPhoneChange(v: String) = _uiState.update { it.copy(phoneDraft = v, phoneError = null) }

    fun onSaveContact() {
        val state = _uiState.value
        if (!PhoneNumberNormalizer.isValid(state.phoneDraft)) {
            _uiState.update { it.copy(phoneError = UiText.StringRes(R.string.contacts_phone_error)) }
            return
        }
        viewModelScope.launch {
            runCatching {
                if (state.editingContact != null) {
                    updateContact(state.editingContact.copy(name = state.nameDraft, phone = state.phoneDraft))
                } else {
                    addContact(state.nameDraft, state.phoneDraft, categoryId)
                }
            }.onSuccess {
                val msg = if (state.editingContact != null) UiText.StringRes(R.string.contacts_toast_updated) else UiText.StringRes(R.string.contacts_toast_saved)
                _uiState.update { it.copy(showAddSheet = false, editingContact = null, toastMessage = msg) }
                _events.emit(ContactsEvent.ContactSaved)
            }.onFailure {
                _events.emit(ContactsEvent.ShowError(UiText.StringRes(R.string.common_error)))
            }
        }
    }

    // Delete with confirmation
    fun onRequestDeleteContact(contact: Contact) =
        _uiState.update { it.copy(confirmDeleteContact = contact) }

    fun onDismissDeleteConfirm() =
        _uiState.update { it.copy(confirmDeleteContact = null) }

    fun onConfirmDeleteContact() {
        val contact = _uiState.value.confirmDeleteContact ?: return
        _uiState.update { it.copy(confirmDeleteContact = null) }
        viewModelScope.launch {
            runCatching { deleteContact(contact) }
                .onSuccess { _uiState.update { it.copy(toastMessage = UiText.StringRes(R.string.contacts_toast_deleted)) } }
                .onFailure { _events.emit(ContactsEvent.ShowError(UiText.StringRes(R.string.common_error))) }
        }
    }

    // Selection mode (kept for backward compat)
    fun onToggleSelection(contactId: String) {
        _uiState.update { state ->
            val updated = if (contactId in state.selectedIds)
                state.selectedIds - contactId
            else
                state.selectedIds + contactId
            state.copy(selectedIds = updated, isSelectionMode = updated.isNotEmpty())
        }
    }

    fun onClearSelection() =
        _uiState.update { it.copy(selectedIds = emptySet(), isSelectionMode = false) }

    fun onDeleteSelected() {
        val toDelete = _uiState.value.let { state ->
            state.contacts.filter { it.id in state.selectedIds }
        }
        viewModelScope.launch {
            runCatching { toDelete.forEach { deleteContact(it) } }
                .onSuccess {
                    onClearSelection()
                    _events.emit(ContactsEvent.SelectionDeleted)
                }
                .onFailure { _events.emit(ContactsEvent.ShowError(UiText.StringRes(R.string.common_error))) }
        }
    }

    fun onDeleteAll() {
        viewModelScope.launch {
            runCatching { deleteAllContacts(categoryId) }
                .onFailure { _events.emit(ContactsEvent.ShowError(UiText.StringRes(R.string.common_error))) }
        }
    }

    fun onDismissToast() = _uiState.update { it.copy(toastMessage = null) }
}
