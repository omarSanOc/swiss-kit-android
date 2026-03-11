package com.epic_engine.swisskit.feature.contacts.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.feature.contacts.domain.model.ContactAction
import com.epic_engine.swisskit.feature.contacts.presentation.components.AddContactSheet
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactRow
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactsEmptyState
import com.epic_engine.swisskit.feature.contacts.presentation.components.SelectionTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    categoryTitle: String,
    onNavigateBack: () -> Unit,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ContactsEvent.LaunchContactAction -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                    context.startActivity(intent)
                }
                is ContactsEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            if (uiState.isSelectionMode) {
                SelectionTopBar(
                    count = uiState.selectedIds.size,
                    onCancel = viewModel::onClearSelection,
                    onDelete = viewModel::onDeleteSelected
                )
            } else {
                TopAppBar(
                    title = { Text(categoryTitle) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "Volver")
                        }
                    },
                    actions = {
                        var showMenu by remember { mutableStateOf(false) }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "Más opciones")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Eliminar todos",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = { showMenu = false; viewModel.onDeleteAll() }
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick = viewModel::onShowAddSheet,
                    containerColor = ContactsDesignTokens.Primary
                ) {
                    Icon(Icons.Default.PersonAdd, "Agregar contacto", tint = Color.White)
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar contacto…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (uiState.contacts.isEmpty()) {
                ContactsEmptyState(message = "No hay contactos en esta categoría")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.contacts, key = { it.id }) { contact ->
                        ContactRow(
                            contact = contact,
                            isSelected = contact.id in uiState.selectedIds,
                            isSelectionMode = uiState.isSelectionMode,
                            onClick = {
                                if (uiState.isSelectionMode) viewModel.onToggleSelection(contact.id)
                            },
                            onLongClick = { viewModel.onToggleSelection(contact.id) },
                            onCall = { viewModel.onContactAction(contact, ContactAction.CALL) },
                            onWhatsApp = { viewModel.onContactAction(contact, ContactAction.WHATSAPP) },
                            onEdit = { viewModel.onEditContact(contact) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddSheet) {
        AddContactSheet(
            nameDraft = uiState.nameDraft,
            phoneDraft = uiState.phoneDraft,
            phoneError = uiState.phoneError,
            isEditing = uiState.editingContact != null,
            onNameChange = viewModel::onNameChange,
            onPhoneChange = viewModel::onPhoneChange,
            onConfirm = viewModel::onSaveContact,
            onDismiss = viewModel::onDismissSheet
        )
    }
}
