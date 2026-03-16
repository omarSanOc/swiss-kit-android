package com.epic_engine.swisskit.feature.notes.presentation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.epic_engine.swisskit.feature.notes.presentation.components.NoteRowCard
import com.epic_engine.swisskit.feature.notes.presentation.components.NotesFAB
import com.epic_engine.swisskit.feature.notes.presentation.components.NotesEmptyState
import com.epic_engine.swisskit.feature.notes.presentation.components.SelectionTopBar
import com.epic_engine.swisskit.feature.notes.presentation.components.SwissKitSearchBar
import com.epic_engine.swisskit.feature.notes.presentation.components.displayTitle
import com.epic_engine.swisskit.feature.notes.presentation.components.notesBackgroundBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNavigateBack: () -> Unit,
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(notesBackgroundBrush())
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (uiState.isSelectionMode) {
                    SelectionTopBar(
                        count = uiState.selectedIds.size,
                        onCancel = viewModel::onClearSelection,
                        onDelete = viewModel::onDeleteSelected
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "Notas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            },
            floatingActionButton = {
                if (!uiState.isSelectionMode) {
                    NotesFAB(
                        onClick = viewModel::onAddNote,
                        modifier = Modifier.padding(end = 0.dp, bottom = 0.dp)
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Search bar — 12 dp top, 24 dp horizontal
                SwissKitSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 12.dp)
                )

                if (uiState.notes.isEmpty() && !uiState.isLoading) {
                    NotesEmptyState(isSearching = uiState.searchQuery.isNotBlank())
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = 24.dp,
                            vertical = 12.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.notes,
                            key = { it.id }
                        ) { note ->
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
    }

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
