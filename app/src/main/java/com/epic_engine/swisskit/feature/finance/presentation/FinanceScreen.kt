package com.epic_engine.swisskit.feature.finance.presentation

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.core.designsystem.components.SwissKitFAB
import com.epic_engine.swisskit.core.designsystem.components.SwissKitSearchBar
import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.presentation.components.FinanceFilterSheet
import com.epic_engine.swisskit.feature.finance.presentation.components.FinanceItemRow
import com.epic_engine.swisskit.feature.finance.presentation.components.FinanceSummaryHeader
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens
import com.epic_engine.swisskit.ui.theme.blueFinance
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Finance?>(null) }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val content = context.contentResolver.openInputStream(it)?.bufferedReader()?.readText()
            content?.let { json -> viewModel.onEvent(FinanceEvent.RestoreJson(json)) }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.pdfBytes.collect { bytes ->
            sharePdfBytes(context, bytes)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.backupJson.collect { json ->
            val fileName = "swisskit_finance_backup_${System.currentTimeMillis()}.json"
            val cacheFile = File(context.cacheDir, fileName)
            cacheFile.writeText(json)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", cacheFile)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Guardar backup"))
        }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(FinanceEvent.ClearMessage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState.isSelectionMode)
                        Text("${uiState.selectedIds.size} seleccionados")
                    else
                        Text("Finanzas")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FinanceDesignTokens.accentBlue.copy(alpha = 0.1f)
                ),
                navigationIcon = {
                    if (uiState.isSelectionMode) {
                        IconButton(onClick = { viewModel.onEvent(FinanceEvent.ClearSelection) }) {
                            Icon(Icons.Default.Close, "Cancelar selección")
                        }
                    }
                },
                actions = {
                    if (uiState.isSelectionMode) {
                        IconButton(onClick = { viewModel.onEvent(FinanceEvent.DeleteSelected) }) {
                            Icon(Icons.Default.Delete, "Eliminar seleccionados", tint = FinanceDesignTokens.expenseColor)
                        }
                    } else {
                        IconButton(onClick = { viewModel.onEvent(FinanceEvent.ToggleFilterSheet) }) {
                            Icon(
                                Icons.Default.FilterList, "Filtros",
                                tint = if (uiState.isFiltered) FinanceDesignTokens.accentBlue else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "Más opciones")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Exportar PDF") },
                                onClick = { viewModel.onEvent(FinanceEvent.ExportPdf); showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Backup JSON") },
                                onClick = { viewModel.onEvent(FinanceEvent.BackupJson); showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Restaurar Backup") },
                                onClick = { openDocumentLauncher.launch(arrayOf("application/json")); showMenu = false }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                SwissKitFAB(
                    onClick = { editingItem = null; showAddSheet = true },
                    colors = listOf(FinanceDesignTokens.accentBlue, FinanceDesignTokens.accentBlue.copy(0.5f))
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = DesignTokens.contentPaddingMedium),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = DesignTokens.contentPaddingMedium)
        ) {
            item {
                FinanceSummaryHeader(
                    totalIncome = uiState.totalIncome,
                    totalExpenses = uiState.totalExpenses,
                    netBalance = uiState.netBalance
                )
            }

            item {
                SwissKitSearchBar(
                    tint = blueFinance,
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.onEvent(FinanceEvent.SearchChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    description = "Buscar transacciones"
                )
            }

            if (!uiState.hasItems) {
                item {
                    SwissKitEmptyView(
                        icon = R.drawable.icon_wallet,
                        title = "Sin transacciones",
                        subtitle = "Agrega tu primera transacción con el botón +",
                        iconTint = FinanceDesignTokens.accentBlue.copy(0.5f),
                        modifier = Modifier.fillMaxWidth().padding(top = 60.dp)
                    )
                }
            }

            items(items = uiState.filteredItems, key = { it.id }) { item ->
                FinanceItemRow(
                    item = item,
                    isSelected = item.id in uiState.selectedIds,
                    isSelectionMode = uiState.isSelectionMode,
                    onClick = {
                        if (uiState.isSelectionMode) viewModel.onEvent(FinanceEvent.ToggleSelection(item.id))
                        else { editingItem = item; showAddSheet = true }
                    },
                    onLongClick = { viewModel.onEvent(FinanceEvent.ToggleSelection(item.id)) }
                )
            }
        }
    }

    if (showAddSheet) {
        EditFinanceSheet(
            existingItem = editingItem,
            onDismiss = { showAddSheet = false; editingItem = null },
            onSaved = { showAddSheet = false; editingItem = null }
        )
    }

    if (uiState.showFilterSheet) {
        FinanceFilterSheet(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onDismiss = { viewModel.onEvent(FinanceEvent.ToggleFilterSheet) }
        )
    }
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
