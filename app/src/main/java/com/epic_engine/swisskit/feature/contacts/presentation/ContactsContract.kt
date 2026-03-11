package com.epic_engine.swisskit.feature.contacts.presentation

import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.domain.model.ContactAction

data class ContactsUiState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val showAddSheet: Boolean = false,
    val editingContact: Contact? = null,
    val nameDraft: String = "",
    val phoneDraft: String = "",
    val phoneError: String? = null,
    val selectedIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false
)

sealed interface ContactsEvent {
    data class LaunchContactAction(val action: ContactAction, val url: String) : ContactsEvent
    data class ShowError(val message: String) : ContactsEvent
    data object ContactSaved : ContactsEvent
    data object SelectionDeleted : ContactsEvent
}
