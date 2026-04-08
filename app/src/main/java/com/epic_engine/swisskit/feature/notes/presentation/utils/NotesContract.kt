package com.epic_engine.swisskit.feature.notes.presentation.utils

import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRecurrence
import com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRequest

// ── Lista ────────────────────────────────────────────────────────────
data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val selectedIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
    val noteToDelete: Note? = null
)

sealed interface NotesEvent {
    data object NavigateToCreate : NotesEvent
    data class NavigateToDetail(val noteId: String) : NotesEvent
    data class ShowError(val message: UiText) : NotesEvent
    data object SelectionDeleted : NotesEvent
    data object NoteDeleted : NotesEvent
}

// ── Detalle / Editor ────────────────────────────────────────────────
data class NoteDetailUiState(
    val note: Note? = null,
    val isEditing: Boolean = false,
    val titleDraft: String = "",
    val contentDraft: String = "",
    val isSaving: Boolean = false,
    val showReminderPicker: Boolean = false,
    val reminderAt: Long? = null,
    val reminderRecurrence: NoteReminderRecurrence? = null,
    val pendingReminderRequest: NoteReminderRequest? = null
)

sealed interface NoteDetailEvent {
    data object Saved : NoteDetailEvent
    data object Deleted : NoteDetailEvent
    data class ShowError(val message: UiText) : NoteDetailEvent
    data class ReminderSet(val at: Long, val recurrence: NoteReminderRecurrence) : NoteDetailEvent
    data object RequestExactAlarmPermission : NoteDetailEvent
}
