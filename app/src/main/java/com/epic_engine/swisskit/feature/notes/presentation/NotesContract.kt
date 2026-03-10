package com.epic_engine.swisskit.feature.notes.presentation

import com.epic_engine.swisskit.feature.notes.domain.model.Note

// ── Lista ────────────────────────────────────────────────────────────
data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val selectedIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false
)

sealed interface NotesEvent {
    data object NavigateToCreate : NotesEvent
    data class NavigateToDetail(val noteId: String) : NotesEvent
    data class ShowError(val message: String) : NotesEvent
    data object SelectionDeleted : NotesEvent
}

// ── Detalle / Editor ────────────────────────────────────────────────
data class NoteDetailUiState(
    val note: Note? = null,
    val isEditing: Boolean = false,
    val titleDraft: String = "",
    val contentDraft: String = "",
    val isSaving: Boolean = false,
    val showMarkdownPreview: Boolean = false,
    val showReminderPicker: Boolean = false,
    val reminderAt: Long? = null
)

sealed interface NoteDetailEvent {
    data object Saved : NoteDetailEvent
    data object Deleted : NoteDetailEvent
    data class ShowError(val message: String) : NoteDetailEvent
    data class ReminderSet(val at: Long) : NoteDetailEvent
}
