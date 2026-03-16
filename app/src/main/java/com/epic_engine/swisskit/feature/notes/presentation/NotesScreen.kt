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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NotesEvent.NavigateToCreate -> onNavigateToCreate()
                is NotesEvent.NavigateToDetail -> onNavigateToDetail(event.noteId)
                is NotesEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                NotesEvent.SelectionDeleted -> snackbarHostState.showSnackbar("Nota eliminada")
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
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.notes,
                            key = { it.id }
                        ) { note ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        viewModel.onDeleteNote(note.id)
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                modifier = Modifier.animateItem(
                                    fadeInSpec = tween(250),
                                    fadeOutSpec = tween(250),
                                    placementSpec = tween(250)
                                ),
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            ) {
                                NoteRowCard(
                                    note = note,
                                    isSelected = note.id in uiState.selectedIds,
                                    isSelectionMode = uiState.isSelectionMode,
                                    onClick = {
                                        if (uiState.isSelectionMode) viewModel.onToggleSelection(note.id)
                                        else viewModel.onOpenNote(note.id)
                                    },
                                    onLongClick = { viewModel.onToggleSelection(note.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
