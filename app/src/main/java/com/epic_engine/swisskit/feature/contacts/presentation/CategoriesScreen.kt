package com.epic_engine.swisskit.feature.contacts.presentation

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
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDesignTokens
import com.epic_engine.swisskit.feature.contacts.presentation.util.CategoriesEvent
import com.epic_engine.swisskit.feature.contacts.presentation.viewmodel.CategoriesViewModel
import com.epic_engine.swisskit.ui.theme.greenContact

@OptIn(ExperimentalMaterial3Api::class)
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
        colors = listOf(ContactsDesignTokens.Primary, ContactsDesignTokens.Background),
        darkColors = listOf(ContactsDesignTokens.Primary, ContactsDesignTokens.DarkBackground),
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Categorías", color = Color.White, fontWeight = FontWeight.Bold)
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
                    val filtered = if (uiState.searchQuery.isBlank()) uiState.categories
                    else uiState.categories.filter {
                        it.title.contains(uiState.searchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(bottom = ContactsDesignTokens.spacingXXXLargePadding),
                        verticalArrangement = Arrangement.spacedBy(ContactsDesignTokens.spacingXSmallPadding)
                    ) {
                        if (uiState.categories.isEmpty()) {
                            item(key = "empty") {
                                SwissKitEmptyView(
                                    icon = R.drawable.icon_folder_plus,
                                    title = "Sin categorías",
                                    subtitle = "Crea tu primera categoría con el botón +",
                                    modifier = Modifier.fillParentMaxSize(),
                                    iconTint = Color.White
                                )
                            }
                        } else {
                            item(key = "search_bar") {
                                SwissKitSearchBar(
                                    tint = greenContact,
                                    query = uiState.searchQuery,
                                    onQueryChange = viewModel::onSearchQueryChange,
                                    description = "Buscar categoría…",
                                    modifier = Modifier.padding(horizontal = ContactsDesignTokens.spacingXXXMediumPadding)
                                )
                            }

                            if (filtered.isEmpty()) {
                                item(key = "empty_filtered") {
                                    SwissKitEmptyView(
                                        icon = R.drawable.icon_folder_plus,
                                        title = "Sin resultados",
                                        subtitle = "Ninguna categoría coincide con tu búsqueda",
                                        modifier = Modifier.fillParentMaxSize(),
                                        iconTint = Color.White
                                    )
                                }
                            } else {
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
                    Text("Eliminar", color = ContactsDesignTokens.ContactsDeleteAction, fontWeight = FontWeight.SemiBold)
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
