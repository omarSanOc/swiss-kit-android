package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialTab(
    scans: List<QRScan>,
    onDeleteScan: (QRScan) -> Unit,
    onCopyContent: (String) -> Unit,
    onOpenContent: (QRScan) -> Unit,
    modifier: Modifier = Modifier
) {
    if (scans.isEmpty()) {
        QREmptyState(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(scans, key = { it.id }) { scan ->
                QRScanItem(
                    scan = scan,
                    onDelete = { onDeleteScan(scan) },
                    onCopy = { onCopyContent(scan.content) },
                    onOpen = { onOpenContent(scan) }
                )
            }
        }
    }
}

@Composable
fun QREmptyState(
    modifier: Modifier = Modifier,
    message: String = "Aún no has escaneado ningún código"
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QRScanItem(
    scan: QRScan,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onOpen: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = scan.type.icon(),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.label,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDate(scan.scannedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onCopy) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar")
            }
            IconButton(onClick = onOpen) {
                Icon(Icons.Default.OpenInNew, contentDescription = "Abrir")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

private fun QRContentType.icon() = when (this) {
    QRContentType.URL -> Icons.Default.Link
    QRContentType.WIFI -> Icons.Default.Wifi
    QRContentType.CONTACT -> Icons.Default.Person
    QRContentType.EMAIL -> Icons.Default.Email
    QRContentType.PHONE -> Icons.Default.Phone
    QRContentType.LOCATION -> Icons.Default.LocationOn
    QRContentType.CALENDAR -> Icons.Default.CalendarMonth
    QRContentType.BARCODE -> Icons.Default.QrCode
    QRContentType.TEXT -> Icons.Default.TextFields
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}
