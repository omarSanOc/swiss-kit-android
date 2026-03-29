package com.epic_engine.swisskit.feature.notes.presentation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.core.designsystem.components.SwissKitBackground
import com.epic_engine.swisskit.core.designsystem.components.SwissKitFAB
import com.epic_engine.swisskit.core.designsystem.components.SwissKitSearchBar
import com.epic_engine.swisskit.feature.notes.presentation.components.NoteRowCard
import com.epic_engine.swisskit.feature.notes.presentation.components.NotesEmptyState
import com.epic_engine.swisskit.feature.notes.presentation.components.displayTitle
import com.epic_engine.swisskit.ui.theme.purpleNotes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var revealedNoteId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NotesEvent.NavigateToCreate -> onNavigateToCreate()
                is NotesEvent.NavigateToDetail -> onNavigateToDetail(event.noteId)
                is NotesEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                NotesEvent.SelectionDeleted -> snackbarHostState.showSnackbar("Notas eliminadas")
                NotesEvent.NoteDeleted -> snackbarHostState.showSnackbar("Nota eliminada")
            }
        }
    }

    SwissKitBackground(
        colors = listOf(NotesColors.Purple, NotesColors.PurpleLight),
        darkColors = listOf(NotesColors.Purple, NotesColors.PurpleDark),
        content = {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text("Notas", color = Color.White, fontWeight = FontWeight.Bold) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        )
                    )
                },
                floatingActionButton = {
                    SwissKitFAB(
                        onClick = viewModel::onAddNote,
                        colors = listOf(NotesColors.FABGradientTop, NotesColors.FABGradientBottom)
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(key = "search_bar") {
                        SwissKitSearchBar(
                            tint = purpleNotes,
                            query = uiState.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChange,
                            modifier = Modifier.padding(top = 12.dp),
                            description = "Buscar notas"
                        )
                    }

                    if (uiState.notes.isEmpty() && !uiState.isLoading) {
                        item(key = "empty") {
                            NotesEmptyState(
                                isSearching = uiState.searchQuery.isNotBlank(),
                                modifier = Modifier.fillParentMaxSize()
                            )
                        }
                    } else {
                        items(uiState.notes, key = { it.id }) { note ->
                            NoteRowCard(
                                note = note,
                                isSelected = note.id in uiState.selectedIds,
                                isSelectionMode = uiState.isSelectionMode,
                                isRevealed = revealedNoteId == note.id,
                                onRevealChange = { revealed ->
                                    revealedNoteId = if (revealed) note.id else null
                                },
                                onClick = {
                                    if (uiState.isSelectionMode) viewModel.onToggleSelection(note.id)
                                    else viewModel.onOpenNote(note.id)
                                },
                                onLongClick = { viewModel.onToggleSelection(note.id) },
                                onDelete = { viewModel.onShowDeleteDialog(note) },
                                modifier = Modifier.animateItem(
                                    fadeInSpec = tween(250),
                                    fadeOutSpec = tween(250),
                                    placementSpec = tween(250)
                                )
                            )
                        }
                    }
                }
            }
        }
    )

    // Diálogo de confirmación: eliminar nota individual
    if (uiState.noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title = { Text("¿Eliminar nota?") },
            text = {
                val title = uiState.noteToDelete!!.displayTitle()
                Text("Esta acción eliminará \"$title\". No se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onConfirmDeleteNote() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDeleteDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
