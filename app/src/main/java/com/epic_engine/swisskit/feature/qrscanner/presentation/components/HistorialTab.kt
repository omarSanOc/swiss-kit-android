package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.presentation.theme.QRScannerDesignTokens


@Composable
fun HistorialTab(
    scans: List<QRScan>,
    showDeleteScanConfirm: QRScan?,
    showDeleteAllConfirm: Boolean,
    onCopyScan: (QRScan) -> Unit,
    onEditLabel: (QRScan) -> Unit,
    onOpenContent: (QRScan) -> Unit,
    onRequestDeleteScan: (QRScan) -> Unit,
    onConfirmDeleteScan: () -> Unit,
    onRequestDeleteAll: () -> Unit,
    onConfirmDeleteAll: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (scans.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SwissKitEmptyView(
                icon = R.drawable.icon_qr,
                title = stringResource(R.string.qr_no_scans_title),
                subtitle = stringResource(R.string.qr_no_scans_subtitle),
                iconTint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = DesignTokens.dimensXXXMedium,
                end = DesignTokens.dimensXXXMedium,
                top = DesignTokens.dimensXXXSmall,
                bottom = QRScannerDesignTokens.dimensLarge
            ),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.dimensXSmall)
        ) {
            item {
                Text(
                    text = stringResource(R.string.qr_history_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = DesignTokens.dimensXXXSmall)
                )
            }
            items(scans, key = { it.id }) { scan ->
                QRScanItem(
                    scan = scan,
                    onCopy = { onCopyScan(scan) },
                    onEditLabel = { onEditLabel(scan) },
                    onOpenContent = { onOpenContent(scan) },
                    onRequestDelete = { onRequestDeleteScan(scan) }
                )
            }
        }
    }

    // Delete single scan confirmation
    showDeleteScanConfirm?.let { scan ->
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text("Eliminar escaneo") },
            text = { Text("¿Eliminar \"${scan.label.ifBlank { scan.content.take(40) }}\"?") },
            confirmButton = {
                TextButton(onClick = onConfirmDeleteScan) { Text(stringResource(R.string.common_delete)) }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) { Text(stringResource(R.string.common_cancel)) }
            }
        )
    }

    // Delete all confirmation
    if (showDeleteAllConfirm) {
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text("Eliminar historial") },
            text = { Text("¿Eliminar todos los escaneos? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = onConfirmDeleteAll) { Text("Eliminar todo") }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) { Text("Cancelar") }
            }
        )
    }
}