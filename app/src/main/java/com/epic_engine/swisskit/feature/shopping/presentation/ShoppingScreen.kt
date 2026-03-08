package com.epic_engine.swisskit.feature.shopping.presentation

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.feature.shopping.presentation.components.ShoppingAddItemBar
import com.epic_engine.swisskit.feature.shopping.presentation.components.ShoppingItemRow
import com.epic_engine.swisskit.ui.theme.yellowShopping

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(ShoppingEvent.ClearMessage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Compras") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = yellowShopping.copy(alpha = 0.1f),
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = {
                        val text = viewModel.buildShareText()
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartir lista"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir lista")
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Desmarcar todos") },
                                leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                                onClick = {
                                    viewModel.onEvent(ShoppingEvent.UncheckAll)
                                    showMenu = false
                                },
                                enabled = uiState.hasCheckedItems
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar marcados") },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                                onClick = {
                                    viewModel.onEvent(ShoppingEvent.DeleteChecked)
                                    showMenu = false
                                },
                                enabled = uiState.hasCheckedItems
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = DesignTokens.contentPaddingMedium)
        ) {
            Spacer(modifier = Modifier.height(DesignTokens.contentPaddingMedium))

            ShoppingAddItemBar(
                value = uiState.inputText,
                onValueChange = { viewModel.onEvent(ShoppingEvent.InputChanged(it)) },
                onAdd = { viewModel.onEvent(ShoppingEvent.AddItem(uiState.inputText)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(DesignTokens.contentPaddingMedium))

            if (!uiState.hasAnyItems) {
                SwissKitEmptyView(
                    icon = Icons.Outlined.ShoppingCart,
                    title = "Tu lista está vacía",
                    subtitle = "Agrega ítems para empezar",
                    modifier = Modifier.fillMaxSize(),
                    iconTint = yellowShopping.copy(alpha = 0.5f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    if (uiState.pendingItems.isNotEmpty()) {
                        item {
                            Text(
                                text = "Pendientes (${uiState.pendingItems.size})",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(
                            items = uiState.pendingItems,
                            key = { it.id }
                        ) { item ->
                            ShoppingItemRow(
                                item = item,
                                onToggle = { viewModel.onEvent(ShoppingEvent.ToggleItem(item)) },
                                onDelete = { viewModel.onEvent(ShoppingEvent.DeleteItem(item)) }
                            )
                        }
                    }

                    if (uiState.pendingItems.isNotEmpty() && uiState.checkedItems.isNotEmpty()) {
                        item {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }

                    if (uiState.checkedItems.isNotEmpty()) {
                        item {
                            Text(
                                text = "Completados (${uiState.checkedItems.size})",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(
                            items = uiState.checkedItems,
                            key = { it.id }
                        ) { item ->
                            ShoppingItemRow(
                                item = item,
                                onToggle = { viewModel.onEvent(ShoppingEvent.ToggleItem(item)) },
                                onDelete = { viewModel.onEvent(ShoppingEvent.DeleteItem(item)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
