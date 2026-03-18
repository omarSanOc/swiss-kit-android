package com.epic_engine.swisskit.feature.contacts.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.core.designsystem.components.SwissKitSearchBar
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactActionSheet
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactRow
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactSheet
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactsBackground
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactsFAB
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactsSearchBar
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactsToast
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDeleteAction
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDimens
import com.epic_engine.swisskit.ui.theme.greenContact

@Composable
fun ContactsScreen(
    categoryTitle: String,
    onNavigateBack: () -> Unit,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var revealedContactId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ContactsEvent.LaunchContactAction -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                    context.startActivity(intent)
                }
                else -> {}
            }
        }
    }

    ContactsBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Toolbar with back button
                ContactsDetailToolbar(
                    title = categoryTitle,
                    onNavigateBack = onNavigateBack
                )

                Spacer(Modifier.height(ContactsDimens.screenTopPadding))

                if (uiState.contacts.isEmpty() && uiState.searchQuery.isBlank()) {
                    // Empty state
                    SwissKitEmptyView(
                        icon = R.drawable.icon_contact_plus,
                        title = "Sin contactos",
                        subtitle = "Crea tu primer contacto con el botón +",
                        modifier = Modifier.fillMaxSize(),
                        iconTint = Color.White
                    )
                } else {
                    // Search bar
                    SwissKitSearchBar(
                        tint = greenContact,
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        description = "Buscar contacto…",
                        modifier = Modifier.padding(horizontal = ContactsDimens.screenHorizontalPadding)
                    )
                    Spacer(Modifier.height(ContactsDimens.rowVerticalInset))

                    if (uiState.contacts.isEmpty()) {
                        SwissKitEmptyView(
                            icon = R.drawable.icon_contact_plus,
                            title = "Sin resultados",
                            subtitle = "Ningún contacto coincide con tu búsqueda",
                            modifier = Modifier.fillMaxSize(),
                            iconTint = Color.White
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                horizontal = ContactsDimens.screenHorizontalPadding,
                                vertical = ContactsDimens.rowVerticalInset
                            ),
                            verticalArrangement = Arrangement.spacedBy(ContactsDimens.rowVerticalInset)
                        ) {
                            items(uiState.contacts, key = { it.id }) { contact ->
                                ContactRow(
                                    contact = contact,
                                    isRevealed = revealedContactId == contact.id,
                                    onRevealChange = { revealed ->
                                        revealedContactId = if (revealed) contact.id else null
                                    },
                                    onShowActionSheet = { viewModel.onShowActionSheet(contact) },
                                    onEdit = { viewModel.onEditContact(contact) },
                                    onDelete = { viewModel.onRequestDeleteContact(contact) },
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

            // FAB
            ContactsFAB(
                onClick = viewModel::onShowAddSheet,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = ContactsDimens.fabMargin, bottom = 56.dp)
            )

            // Toast
            ContactsToast(
                message = uiState.toastMessage,
                onDismiss = viewModel::onDismissToast
            )
        }
    }

    // Add/Edit sheet
    if (uiState.showAddSheet) {
        ContactSheet(
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

    // Action sheet
    uiState.actionSheetContact?.let { contact ->
        ContactActionSheet(
            contact = contact,
            onAction = { action -> viewModel.onContactAction(contact, action) },
            onDismiss = viewModel::onDismissActionSheet
        )
    }

    // Delete confirmation dialog
    uiState.confirmDeleteContact?.let { contact ->
        AlertDialog(
            onDismissRequest = viewModel::onDismissDeleteConfirm,
            title = { Text("¿Eliminar contacto?") },
            text = {
                Text("Se eliminará \"${contact.name}\" de tu lista de contactos.")
            },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmDeleteContact) {
                    Text("Eliminar", color = ContactsDeleteAction, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissDeleteConfirm) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ContactsDetailToolbar(
    title: String,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = ContactsDimens.screenHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(Modifier.weight(1f))
        // Spacer to balance back button
        Spacer(Modifier.size(48.dp))
    }
}
