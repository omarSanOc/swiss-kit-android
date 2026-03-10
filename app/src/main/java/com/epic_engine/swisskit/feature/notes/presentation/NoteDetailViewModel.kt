package com.epic_engine.swisskit.feature.notes.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.domain.usecase.DeleteNoteUseCase
import com.epic_engine.swisskit.feature.notes.domain.usecase.GetNoteByIdUseCase
import com.epic_engine.swisskit.feature.notes.domain.usecase.SaveNoteUseCase
import com.epic_engine.swisskit.feature.notes.domain.usecase.SetNoteReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteById: GetNoteByIdUseCase,
    private val saveNote: SaveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
    private val setReminder: SetNoteReminderUseCase
) : ViewModel() {

    private val noteId: String? = savedStateHandle["noteId"]

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NoteDetailEvent>()
    val events: SharedFlow<NoteDetailEvent> = _events.asSharedFlow()

    init {
        loadNote()
    }

    private fun loadNote() {
        viewModelScope.launch {
            val note = noteId?.let { getNoteById(it) }
            _uiState.update {
                if (note != null) {
                    it.copy(
                        note = note,
                        titleDraft = note.title,
                        contentDraft = note.content,
                        reminderAt = note.reminderAt,
                        isEditing = false
                    )
                } else {
                    it.copy(isEditing = true)
                }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(titleDraft = value) }
    fun onContentChange(value: String) = _uiState.update { it.copy(contentDraft = value) }
    fun onTogglePreview() = _uiState.update { it.copy(showMarkdownPreview = !it.showMarkdownPreview) }
    fun onToggleEdit() = _uiState.update { it.copy(isEditing = !it.isEditing) }
    fun onShowReminderPicker() = _uiState.update { it.copy(showReminderPicker = true) }
    fun onDismissReminderPicker() = _uiState.update { it.copy(showReminderPicker = false) }

    fun onSave() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            runCatching {
                val draft = Note(
                    id = state.note?.id ?: "",
                    title = state.titleDraft.trim(),
                    content = state.contentDraft,
                    createdAt = state.note?.createdAt ?: 0L,
                    updatedAt = 0L,
                    reminderAt = state.reminderAt
                )
                saveNote(draft)
            }.onSuccess { saved ->
                _uiState.update { s -> s.copy(isSaving = false, isEditing = false, note = saved) }
                _events.emit(NoteDetailEvent.Saved)
            }.onFailure {
                _uiState.update { s -> s.copy(isSaving = false) }
                _events.emit(NoteDetailEvent.ShowError(it.message ?: "Error al guardar"))
            }
        }
    }

    fun onDelete() {
        val note = _uiState.value.note ?: return
        viewModelScope.launch {
            runCatching { deleteNote(note) }
                .onSuccess { _events.emit(NoteDetailEvent.Deleted) }
                .onFailure { _events.emit(NoteDetailEvent.ShowError(it.message ?: "Error")) }
        }
    }

    fun onSetReminder(epochMillis: Long) {
        val note = _uiState.value.note ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(showReminderPicker = false) }
            runCatching { setReminder(note, epochMillis) }
                .onSuccess {
                    _uiState.update { s -> s.copy(reminderAt = epochMillis) }
                    _events.emit(NoteDetailEvent.ReminderSet(epochMillis))
                }
                .onFailure { _events.emit(NoteDetailEvent.ShowError(it.message ?: "Error")) }
        }
    }

    fun onClearReminder() {
        val note = _uiState.value.note ?: return
        viewModelScope.launch {
            runCatching { setReminder(note, null) }
                .onSuccess { _uiState.update { it.copy(reminderAt = null) } }
                .onFailure { _events.emit(NoteDetailEvent.ShowError(it.message ?: "Error")) }
        }
    }
}
