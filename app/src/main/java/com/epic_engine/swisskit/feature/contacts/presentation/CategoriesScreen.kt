package com.epic_engine.swisskit.feature.contacts.presentation

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.components.SwissKitBackground
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.core.designsystem.components.SwissKitFAB
import com.epic_engine.swisskit.core.designsystem.components.SwissKitSearchBar
import com.epic_engine.swisskit.core.designsystem.components.SwissKitToast
import com.epic_engine.swisskit.feature.contacts.presentation.components.CategoryRow
import com.epic_engine.swisskit.feature.contacts.presentation.components.CategorySheet
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDeleteAction
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDimens
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsFABGradientBottom
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsFABGradientTop
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTeal
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTealDark
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTealLight
import com.epic_engine.swisskit.ui.theme.greenContact

@Composable
fun CategoriesScreen(
    onNavigateToContacts: (categoryId: String, categoryTitle: String) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var revealedCategoryId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CategoriesEvent.NavigateToContacts ->
                    onNavigateToContacts(event.categoryId, event.categoryTitle)
                else -> {}
            }
        }
    }

    SwissKitBackground(
        colors = listOf(ContactsTeal, ContactsTealLight),
        darkColors = listOf(ContactsTeal, ContactsTealDark),
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    // Toolbar
                    ContactsToolbar(title = "Contactos")

                    Spacer(Modifier.height(ContactsDimens.screenTopPadding))

                    val filtered = if (uiState.searchQuery.isBlank()) uiState.categories
                    else uiState.categories.filter {
                        it.title.contains(uiState.searchQuery, ignoreCase = true)
                    }

                    if (uiState.categories.isEmpty()) {
                        // Empty state — no search bar, just the empty view
                        SwissKitEmptyView(
                            icon = R.drawable.icon_folder_plus,
                            title = "Sin categorías",
                            subtitle = "Crea tu primera categoría con el botón +",
                            modifier = Modifier.fillMaxSize(),
                            iconTint = Color.White
                        )
                    } else {
                        // Search bar
                        SwissKitSearchBar(
                            tint = greenContact,
                            query = uiState.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChange,
                            description = "Buscar categoría…",
                            modifier = Modifier.padding(horizontal = ContactsDimens.screenHorizontalPadding)
                        )
                        Spacer(Modifier.height(ContactsDimens.rowVerticalInset))

                        if (filtered.isEmpty()) {
                            SwissKitEmptyView(
                                icon = R.drawable.icon_folder_plus,
                                title = "Sin resultados",
                                subtitle = "Ninguna categoría coincide con tu búsqueda",
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
                                items(filtered, key = { it.id }) { category ->
                                    CategoryRow(
                                        category = category,
                                        isRevealed = revealedCategoryId == category.id,
                                        onRevealChange = { revealed ->
                                            revealedCategoryId = if (revealed) category.id else null
                                        },
                                        onClick = { viewModel.onSelectCategory(category.id, category.title) },
                                        onRename = { viewModel.onStartRename(category) },
                                        onDelete = { viewModel.onRequestDeleteCategory(category) },
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
                SwissKitFAB(
                    onClick = viewModel::onShowAddSheet,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = ContactsDimens.fabMargin, bottom = 56.dp),
                    colors = listOf(ContactsFABGradientTop, ContactsFABGradientBottom)
                )

                // Toast
                SwissKitToast(
                    message = uiState.toastMessage,
                    onDismiss = viewModel::onDismissToast
                )
            }
        }
    )

    // Add/Rename sheet
    if (uiState.showAddSheet) {
        CategorySheet(
            title = uiState.addCategoryTitle,
            onTitleChange = viewModel::onAddCategoryTitleChange,
            onConfirm = viewModel::onConfirmAdd,
            onDismiss = viewModel::onDismissAddSheet,
            isRenaming = false
        )
    }

    uiState.renamingCategory?.let {
        CategorySheet(
            title = uiState.renameTitle,
            onTitleChange = viewModel::onRenameTitleChange,
            onConfirm = viewModel::onConfirmRename,
            onDismiss = viewModel::onDismissRename,
            isRenaming = true
        )
    }

    // Delete confirmation dialog
    uiState.confirmDeleteCategory?.let { category ->
        AlertDialog(
            onDismissRequest = viewModel::onDismissDeleteConfirm,
            title = { Text("¿Eliminar categoría?") },
            text = {
                Text("Se eliminará \"${category.title}\" y todos sus contactos. Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmDeleteCategory) {
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
private fun ContactsToolbar(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ContactsDimens.screenHorizontalPadding)
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}
