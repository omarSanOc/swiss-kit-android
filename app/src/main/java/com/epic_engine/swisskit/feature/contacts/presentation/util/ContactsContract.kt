package com.epic_engine.swisskit.feature.contacts.presentation.util

import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.domain.model.ContactAction

data class ContactsUiState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    // Add/Edit sheet
    val showAddSheet: Boolean = false,
    val editingContact: Contact? = null,
    val nameDraft: String = "",
    val phoneDraft: String = "",
    val phoneError: String? = null,
    // Action sheet (call / WhatsApp)
    val actionSheetContact: Contact? = null,
    // Delete confirmation
    val confirmDeleteContact: Contact? = null,
    // Selection mode (kept for backward compat)
    val selectedIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
    // Toast
    val toastMessage: String? = null
)

sealed interface ContactsEvent {
    data class LaunchContactAction(val action: ContactAction, val url: String) : ContactsEvent
    data class ShowError(val message: String) : ContactsEvent
    data object ContactSaved : ContactsEvent
    data object SelectionDeleted : ContactsEvent
}
