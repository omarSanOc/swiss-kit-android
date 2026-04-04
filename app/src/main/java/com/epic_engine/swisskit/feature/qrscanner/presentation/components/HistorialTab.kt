package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.presentation.theme.QRScannerDesignTokens
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt



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
                title = "Sin escaneos",
                subtitle = "Escanea un código para empezar",
                iconTint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = QRScannerDesignTokens.screenHorizontalPadding,
                end = QRScannerDesignTokens.screenHorizontalPadding,
                top = 4.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Historial",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 4.dp)
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
                TextButton(onClick = onConfirmDeleteScan) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = onDismissDialog) { Text("Cancelar") }
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