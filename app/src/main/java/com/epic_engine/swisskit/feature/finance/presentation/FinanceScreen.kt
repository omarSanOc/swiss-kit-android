package com.epic_engine.swisskit.feature.finance.presentation

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.components.SwissKitBackground
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.core.designsystem.components.SwissKitFAB
import com.epic_engine.swisskit.core.designsystem.components.SwissKitSearchBar
import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceSortOrder
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.presentation.components.FinanceInlineFilterPanel
import com.epic_engine.swisskit.feature.finance.presentation.components.FinanceItemRow
import com.epic_engine.swisskit.feature.finance.presentation.components.FinanceToggleButton
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    onNavigateToEditor: (String?) -> Unit,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Finance?>(null) }
    var showDeleteSelectedAlert by remember { mutableStateOf(false) }
    var revealedItemId by remember { mutableStateOf<String?>(null) }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }
    val scope = rememberCoroutineScope()
    var pendingBackupJson by remember { mutableStateOf<String?>(null) }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val content = context.contentResolver.openInputStream(it)?.bufferedReader()?.readText()
            content?.let { json -> viewModel.onEvent(FinanceEvent.RestoreJson(json)) }
        }
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            pendingBackupJson?.let { json ->
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    stream.write(json.toByteArray())
                }
                pendingBackupJson = null
                scope.launch { snackbarHostState.showSnackbar("Archivo guardado") }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.pdfBytes.collect { bytes -> sharePdfBytes(context, bytes) }
    }

    LaunchedEffect(Unit) {
        viewModel.backupJson.collect { json ->
            pendingBackupJson = json
            createDocumentLauncher.launch("swisskit_finance_backup_${System.currentTimeMillis()}.json")
        }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(FinanceEvent.ClearMessage)
        }
    }

    // Delete confirmation dialog
    itemToDelete?.let { item ->
        val typeLabel = if (item.type == FinanceType.EXPENSE) "gasto" else "ingreso"
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "¿Eliminar $typeLabel?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Esta acción eliminará esta transacción. ¿Estás seguro de continuar?",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(FinanceEvent.DeleteItem(item))
                    itemToDelete = null
                }) {
                    Text("Eliminar", color = Color(0xFFFF3833))
                }
            },
            dismissButton = {
                Button(
                    onClick = { itemToDelete = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDeleteSelectedAlert) {
        AlertDialog(
            onDismissRequest = { showDeleteSelectedAlert = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("¿Eliminar transacciones?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Se eliminarán ${uiState.selectedVisibleCount} transacciones seleccionadas. ¿Estás seguro de continuar?",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(FinanceEvent.DeleteSelected)
                    showDeleteSelectedAlert = false
                }) { Text("Eliminar", color = Color(0xFFFF3833)) }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteSelectedAlert = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancelar") }
            }
        )
    }

    SwissKitBackground(colors = listOf(
        FinanceDesignTokens.primaryBlue,
        FinanceDesignTokens.backgroundLight,
    ),
        darkColors =  listOf(
            FinanceDesignTokens.primaryBlue,
            FinanceDesignTokens.backgroundDark
        ),
    content = {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {
                            if (uiState.isSelectionMode)
                                Text(
                                    "${uiState.selectedIds.size} seleccionados",
                                    color = Color.White
                                )
                            else
                                Text("Finanzas", color = Color.White, fontWeight = FontWeight.Bold)
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent
                        ),
                        navigationIcon = {
                            if (uiState.isSelectionMode) {
                                IconButton(onClick = { viewModel.onEvent(FinanceEvent.ClearSelection) }) {
                                    Icon(Icons.Default.Close, "Cancelar selección", tint = Color.White)
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, "Más opciones", tint = Color.White)
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                if (uiState.isSelectionMode) {
                                    DropdownMenuItem(
                                        text = { Text("Salir del modo de selección") },
                                        onClick = {
                                            viewModel.onEvent(FinanceEvent.ClearSelection)
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        enabled = uiState.filteredItems.isNotEmpty(),
                                        text = {
                                            Text(if (uiState.allVisibleSelected) "Deseleccionar todo" else "Seleccionar todo")
                                        },
                                        onClick = {
                                            viewModel.onEvent(
                                                if (uiState.allVisibleSelected) FinanceEvent.DeselectAll
                                                else FinanceEvent.SelectAll
                                            )
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        enabled = uiState.canDeleteSelected,
                                        text = {
                                            Text(
                                                "Eliminar ${uiState.selectedVisibleCount} transacciones seleccionadas",
                                                color = if (uiState.canDeleteSelected) Color(0xFFFF3833)
                                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                            )
                                        },
                                        onClick = {
                                            showDeleteSelectedAlert = true
                                            showMenu = false
                                        }
                                    )
                                } else {
                                    DropdownMenuItem(
                                        text = { Text("Exportar a archivo") },
                                        onClick = {
                                            viewModel.onEvent(FinanceEvent.BackupJson)
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Importar desde archivo") },
                                        onClick = {
                                            openDocumentLauncher.launch(arrayOf("application/json"))
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Compartir en PDF") },
                                        onClick = {
                                            viewModel.onEvent(FinanceEvent.ExportPdf)
                                            showMenu = false
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        enabled = uiState.hasItems,
                                        text = { Text("Entrar en modo de selección") },
                                        onClick = {
                                            viewModel.onEvent(FinanceEvent.EnterSelectionMode)
                                            showMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    )
                },
                floatingActionButton = {
                    if (!uiState.isSelectionMode) {
                        SwissKitFAB(
                            onClick = { onNavigateToEditor(null) },
                            colors = listOf(
                                FinanceDesignTokens.gradientStart,
                                FinanceDesignTokens.primaryBlue
                            )
                        )
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(bottom = 88.dp),
                ) {
                    // Search bar + Filter + Sort row
                    item(key = "header_search") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SwissKitSearchBar(
                                tint = FinanceDesignTokens.primaryBlue,
                                query = uiState.searchQuery,
                                onQueryChange = { viewModel.onEvent(FinanceEvent.SearchChanged(it)) },
                                modifier = Modifier.weight(1f),
                                description = "Buscar"
                            )

                            // Filter toggle button
                            val isFilterActive = uiState.showFilterSheet || uiState.isFiltered
                            FinanceToggleButton(
                                isActive = isFilterActive,
                                onClick = { viewModel.onEvent(FinanceEvent.ToggleFilterSheet) },
                                icon = painterResource(R.drawable.icon_filter),
                                contentDescription = "Filtros"
                            )

                            // Sort toggle button
                            val isSortActive = uiState.sortOrder == FinanceSortOrder.ASCENDING
                            FinanceToggleButton(
                                isActive = isSortActive,
                                onClick = {
                                    val newOrder = if (uiState.sortOrder == FinanceSortOrder.DESCENDING)
                                        FinanceSortOrder.ASCENDING else FinanceSortOrder.DESCENDING
                                    viewModel.onEvent(FinanceEvent.ToggleSortOrder(newOrder))
                                },
                                icon = if (uiState.sortOrder == FinanceSortOrder.DESCENDING)
                                    painterResource(R.drawable.icon_arrow_down)
                                else painterResource(R.drawable.icon_arrow_up),
                                contentDescription = "Ordenar"
                            )
                        }
                    }

                    // Inline filter panel
                    item(key = "header_filter") {
                        FinanceInlineFilterPanel(
                            visible = uiState.showFilterSheet,
                            availableCategories = uiState.availableCategories,
                            selectedCategories = uiState.selectedCategories,
                            onToggleCategoryFilter = { viewModel.onEvent(FinanceEvent.ToggleCategoryFilter(it)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp)
                        )
                    }

                    // Total row
                    item(key = "header_total") {
                        val filteredNet = uiState.filteredItems.sumOf {
                            if (it.type == FinanceType.INCOME) it.amount else -it.amount
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = FinanceDesignTokens.headerTextAlpha)
                            )
                            Text(
                                text = currencyFormat.format(filteredNet),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Empty state or transaction list
                    if (!uiState.hasItems) {
                        item(key = "empty_state") {
                            SwissKitEmptyView(
                                icon = R.drawable.icon_not_money,
                                title = "Sin transacciones",
                                subtitle = "Agrega tu primera transacción con el botón +",
                                iconTint = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 60.dp)
                            )
                        }
                    } else {
                        items(
                            items = uiState.filteredItems,
                            key = { it.id }
                        ) { finance ->
                            FinanceItemRow(
                                item = finance,
                                isSelected = finance.id in uiState.selectedIds,
                                isSelectionMode = uiState.isSelectionMode,
                                isRevealed = revealedItemId == finance.id,
                                onRevealChange = { revealed ->
                                    revealedItemId = if (revealed) finance.id else null
                                },
                                onClick = {
                                    if (uiState.isSelectionMode) viewModel.onEvent(FinanceEvent.ToggleSelection(finance.id))
                                    else onNavigateToEditor(finance.id)
                                },
                                onDeleteRequest = { itemToDelete = finance },
                                modifier = Modifier
                                    .animateItem()
                                    .padding(
                                        horizontal = FinanceDesignTokens.listRowInset,
                                        vertical = FinanceDesignTokens.listRowGap
                                    )
                            )
                        }
                    }
                }
            }
        }
    })
}

private fun sharePdfBytes(context: Context, bytes: ByteArray) {
    val file = File(context.cacheDir, "swisskit_finanzas_${System.currentTimeMillis()}.pdf")
    FileOutputStream(file).use { it.write(bytes) }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
}
