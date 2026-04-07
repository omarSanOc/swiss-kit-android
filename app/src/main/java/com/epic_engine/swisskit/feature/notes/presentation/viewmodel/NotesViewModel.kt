package com.epic_engine.swisskit.feature.notes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.domain.usecase.DeleteNotesUseCase
import com.epic_engine.swisskit.feature.notes.domain.usecase.ObserveNotesUseCase
import com.epic_engine.swisskit.feature.notes.domain.usecase.SearchNotesUseCase
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.feature.notes.presentation.utils.NotesEvent
import com.epic_engine.swisskit.feature.notes.presentation.utils.NotesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
class NotesViewModel @Inject constructor(
    private val observeNotes: ObserveNotesUseCase,
    private val searchNotes: SearchNotesUseCase,
    private val deleteNotes: DeleteNotesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NotesEvent>()
    val events: SharedFlow<NotesEvent> = _events.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        observeNotesList()
    }

    @OptIn(FlowPreview::class)
    private fun observeNotesList() {
        _searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isBlank()) observeNotes() else searchNotes(query)
            }
            .onEach { notes ->
                _uiState.update { it.copy(notes = notes, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onToggleSelection(noteId: String) {
        _uiState.update { state ->
            val updated = if (noteId in state.selectedIds)
                state.selectedIds - noteId
            else
                state.selectedIds + noteId
            state.copy(
                selectedIds = updated,
                isSelectionMode = updated.isNotEmpty()
            )
        }
    }

    fun onClearSelection() {
        _uiState.update { it.copy(selectedIds = emptySet(), isSelectionMode = false) }
    }

    fun onDeleteSelected() {
        val ids = _uiState.value.selectedIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            runCatching { deleteNotes(ids) }
                .onSuccess {
                    onClearSelection()
                    _events.emit(NotesEvent.SelectionDeleted)
                }
                .onFailure { _events.emit(NotesEvent.ShowError(UiText.StringRes(R.string.common_error))) }
        }
    }

    fun onAddNote() = viewModelScope.launch { _events.emit(NotesEvent.NavigateToCreate) }

    fun onOpenNote(noteId: String) =
        viewModelScope.launch { _events.emit(NotesEvent.NavigateToDetail(noteId)) }

    /** Muestra el diálogo de confirmación guardando la nota a eliminar. */
    fun onShowDeleteDialog(note: Note) {
        _uiState.update { it.copy(noteToDelete = note) }
    }

    /** Descarta el diálogo sin eliminar nada. */
    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(noteToDelete = null) }
    }

    /** Confirma la eliminación de la nota almacenada en noteToDelete. */
    fun onConfirmDeleteNote() {
        val note = _uiState.value.noteToDelete ?: return
        _uiState.update { it.copy(noteToDelete = null) }
        viewModelScope.launch {
            runCatching { deleteNotes(listOf(note.id)) }
                .onSuccess { _events.emit(NotesEvent.NoteDeleted) }
                .onFailure { _events.emit(NotesEvent.ShowError(UiText.StringRes(R.string.notes_error_delete))) }
        }
    }
}
