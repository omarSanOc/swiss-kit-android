package com.epic_engine.swisskit.feature.contacts.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.feature.contacts.presentation.components.AddCategorySheet
import com.epic_engine.swisskit.feature.contacts.presentation.components.CategoryCard
import com.epic_engine.swisskit.feature.contacts.presentation.components.ContactsEmptyState
import com.epic_engine.swisskit.feature.contacts.presentation.components.RenameDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onNavigateToContacts: (categoryId: String, categoryTitle: String) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CategoriesEvent.NavigateToContacts ->
                    onNavigateToContacts(event.categoryId, event.categoryTitle)
                is CategoriesEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contactos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onShowAddSheet,
                containerColor = ContactsDesignTokens.Primary
            ) {
                Icon(Icons.Default.Add, "Nueva categoría", tint = Color.White)
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
                placeholder = { Text("Buscar categoría…") },
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

            val filtered = if (uiState.searchQuery.isBlank()) uiState.categories
            else uiState.categories.filter {
                it.title.contains(uiState.searchQuery, ignoreCase = true)
            }

            if (filtered.isEmpty()) {
                ContactsEmptyState(
                    message = if (uiState.searchQuery.isNotBlank()) "Sin resultados"
                    else "No hay categorías. Agrega una con el botón +"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { category ->
                        CategoryCard(
                            category = category,
                            onClick = { viewModel.onSelectCategory(category.id, category.title) },
                            onRename = { viewModel.onStartRename(category) },
                            onDelete = { viewModel.onDeleteCategory(category.id) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddSheet) {
        AddCategorySheet(
            title = uiState.addCategoryTitle,
            onTitleChange = viewModel::onAddCategoryTitleChange,
            onConfirm = viewModel::onConfirmAdd,
            onDismiss = viewModel::onDismissAddSheet
        )
    }

    uiState.renamingCategory?.let {
        RenameDialog(
            title = uiState.renameTitle,
            onTitleChange = viewModel::onRenameTitleChange,
            onConfirm = viewModel::onConfirmRename,
            onDismiss = viewModel::onDismissRename
        )
    }
}
