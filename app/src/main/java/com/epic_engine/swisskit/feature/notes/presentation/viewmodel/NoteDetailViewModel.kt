package com.epic_engine.swisskit.feature.notes.presentation.viewmodel

import android.app.AlarmManager
import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRequest
import com.epic_engine.swisskit.feature.notes.domain.usecase.DeleteNoteUseCase
import com.epic_engine.swisskit.feature.notes.domain.usecase.GetNoteByIdUseCase
import com.epic_engine.swisskit.feature.notes.domain.usecase.SaveNoteUseCase
import com.epic_engine.swisskit.feature.notes.domain.usecase.SetNoteReminderUseCase
import com.epic_engine.swisskit.feature.notes.presentation.utils.NoteDetailEvent
import com.epic_engine.swisskit.feature.notes.presentation.utils.NoteDetailUiState
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
    private val setReminder: SetNoteReminderUseCase,
    private val alarmManager: AlarmManager
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
                        reminderRecurrence = note.reminderRecurrence,
                        isEditing = true
                    )
                } else {
                    it.copy(isEditing = true)
                }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(titleDraft = value) }
    fun onContentChange(value: String) = _uiState.update { it.copy(contentDraft = value) }
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
                    reminderAt = state.reminderAt,
                    reminderRecurrence = state.reminderRecurrence
                )
                saveNote(draft)
            }.onSuccess { saved ->
                _uiState.update { s -> s.copy(isSaving = false, isEditing = false, note = saved) }
                _events.emit(NoteDetailEvent.Saved)
            }.onFailure {
                _uiState.update { s -> s.copy(isSaving = false) }
                _events.emit(NoteDetailEvent.ShowError(UiText.StringRes(R.string.note_detail_error_save)))
            }
        }
    }

    fun onDelete() {
        val note = _uiState.value.note ?: return
        viewModelScope.launch {
            runCatching { deleteNote(note) }
                .onSuccess { _events.emit(NoteDetailEvent.Deleted) }
                .onFailure { _events.emit(NoteDetailEvent.ShowError(UiText.StringRes(R.string.common_error))) }
        }
    }

    /**
     * Called from the Screen after POST_NOTIFICATIONS has been confirmed.
     * Checks exact alarm permission: if missing, stores request and requests access.
     * If granted, schedules immediately.
     */
    fun onSetReminder(request: NoteReminderRequest) {
        val note = _uiState.value.note ?: return
        _uiState.update { it.copy(showReminderPicker = false) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            _uiState.update { it.copy(pendingReminderRequest = request) }
            viewModelScope.launch { _events.emit(NoteDetailEvent.RequestExactAlarmPermission) }
            return
        }

        scheduleReminder(note, request)
    }

    /**
     * Called from the Screen on Lifecycle.Event.ON_RESUME when the user returns
     * from the exact alarm settings page.
     */
    fun onResumeCheckPendingReminder() {
        val request = _uiState.value.pendingReminderRequest ?: return
        val note = _uiState.value.note ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) return

        _uiState.update { it.copy(pendingReminderRequest = null) }
        scheduleReminder(note, request)
    }

    private fun scheduleReminder(note: Note, request: NoteReminderRequest) {
        viewModelScope.launch {
            runCatching { setReminder(note, request) }
                .onSuccess {
                    _uiState.update { s ->
                        s.copy(
                            reminderAt = request.triggerAtMillis,
                            reminderRecurrence = request.recurrence
                        )
                    }
                    _events.emit(NoteDetailEvent.ReminderSet(request.triggerAtMillis, request.recurrence))
                }
                .onFailure { _events.emit(NoteDetailEvent.ShowError(UiText.StringRes(R.string.common_error))) }
        }
    }

    fun onClearReminder() {
        val note = _uiState.value.note ?: return
        viewModelScope.launch {
            runCatching { setReminder(note, null) }
                .onSuccess {
                    _uiState.update { it.copy(reminderAt = null, reminderRecurrence = null) }
                }
                .onFailure { _events.emit(NoteDetailEvent.ShowError(UiText.StringRes(R.string.common_error))) }
        }
    }

    /**
     * Saves the current draft silently (without navigating back), then opens the reminder sheet.
     * Used from the overflow menu "Guardar como recordatorio".
     */
    fun onSaveAndShowReminder() {
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
                    reminderAt = state.reminderAt,
                    reminderRecurrence = state.reminderRecurrence
                )
                saveNote(draft)
            }.onSuccess { saved ->
                _uiState.update { s ->
                    s.copy(isSaving = false, note = saved, showReminderPicker = true)
                }
            }.onFailure {
                _uiState.update { s -> s.copy(isSaving = false) }
                _events.emit(NoteDetailEvent.ShowError(UiText.StringRes(R.string.note_detail_error_save)))
            }
        }
    }
}
