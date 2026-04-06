package com.epic_engine.swisskit.feature.contacts.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitBackground
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.core.designsystem.components.SwissKitFAB
import com.epic_engine.swisskit.core.designsystem.components.SwissKitSearchBar
import com.epic_engine.swisskit.core.designsystem.components.SwissKitToast
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactActionSheet
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactRow
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactSheet
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDesignTokens
import com.epic_engine.swisskit.feature.contacts.presentation.util.ContactsEvent
import com.epic_engine.swisskit.feature.contacts.presentation.viewmodel.ContactsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    categoryTitle: String,
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

    SwissKitBackground(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(categoryTitle, color = Color.White, fontWeight = FontWeight.Bold)
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = Color.Transparent
                            )
                        )
                    },
                    floatingActionButton = {
                        SwissKitFAB(
                            onClick = viewModel::onShowAddSheet,
                            colors = listOf(ContactsDesignTokens.ContactsFABGradientTop,
                                ContactsDesignTokens.ContactsFABGradientBottom)
                        )
                    }
                ) { padding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(bottom = ContactsDesignTokens.spacingXXXLargePadding),
                        verticalArrangement = Arrangement.spacedBy(ContactsDesignTokens.spacingXSmallPadding)
                    ) {
                        if (uiState.contacts.isEmpty() && uiState.searchQuery.isBlank()) {
                            item(key = "empty") {
                                SwissKitEmptyView(
                                    icon = R.drawable.icon_contact_plus,
                                    title = "Sin contactos",
                                    subtitle = "Crea tu primer contacto con el botón +",
                                    modifier = Modifier.fillParentMaxSize(),
                                    iconTint = Color.White
                                )
                            }
                        } else {
                            item(key = "search_bar") {
                                SwissKitSearchBar(
                                    tint = ContactsDesignTokens.Primary,
                                    query = uiState.searchQuery,
                                    onQueryChange = viewModel::onSearchQueryChange,
                                    description = "Buscar contacto…",
                                    modifier = Modifier.padding(horizontal = ContactsDesignTokens.spacingXXXMediumPadding)
                                )
                            }

                            if (uiState.contacts.isEmpty()) {
                                item(key = "empty_filtered") {
                                    SwissKitEmptyView(
                                        icon = R.drawable.icon_contact_plus,
                                        title = "Sin resultados",
                                        subtitle = "Ningún contacto coincide con tu búsqueda",
                                        modifier = Modifier.fillParentMaxSize(),
                                        iconTint = Color.White
                                    )
                                }
                            } else {
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
                                        modifier = Modifier
                                            .padding(horizontal = ContactsDesignTokens.spacingXXXMediumPadding)
                                            .animateItem(
                                                fadeInSpec = tween(ContactsDesignTokens.animationTimeMs),
                                                fadeOutSpec = tween(ContactsDesignTokens.animationTimeMs),
                                                placementSpec = tween(ContactsDesignTokens.animationTimeMs)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                // Toast
                SwissKitToast(
                    message = uiState.toastMessage,
                    onDismiss = viewModel::onDismissToast
                )
            }
        },
        colors = listOf(ContactsDesignTokens.Primary, ContactsDesignTokens.Background),
        darkColors = listOf(ContactsDesignTokens.Primary, ContactsDesignTokens.DarkBackground),
    )

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
                    Text("Eliminar", color = DesignTokens.deleteColor, fontWeight = FontWeight.SemiBold)
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
