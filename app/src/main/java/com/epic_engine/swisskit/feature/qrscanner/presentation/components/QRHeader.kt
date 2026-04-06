package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.qrscanner.domain.model.ScanMode
import com.epic_engine.swisskit.feature.qrscanner.presentation.theme.QRScannerDesignTokens

@Composable
fun QRHeader(
    selectedIndex: Int,
    scanMode: ScanMode,
    hasScans: Boolean,
    showOverflowMenu: Boolean,
    onOpenOverflow: () -> Unit,
    onDismissOverflow: () -> Unit,
    onSetScanMode: (ScanMode) -> Unit,
    onRequestDeleteAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Código QR",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )
        if (selectedIndex == 0) {
            Box {
                IconButton(onClick = onOpenOverflow) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = showOverflowMenu,
                    onDismissRequest = onDismissOverflow
                ) {
                    Text(
                        text = "Modo de escaneo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    DropdownMenuItem(
                        text = { Text("Normal") },
                        leadingIcon = {
                            if (scanMode == ScanMode.SINGLE) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = QRScannerDesignTokens.Primary
                                )
                            }
                        },
                        onClick = { onSetScanMode(ScanMode.SINGLE) }
                    )
                    DropdownMenuItem(
                        text = { Text("Continuo") },
                        leadingIcon = {
                            if (scanMode == ScanMode.CONTINUOUS) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = QRScannerDesignTokens.Primary
                                )
                            }
                        },
                        onClick = { onSetScanMode(ScanMode.CONTINUOUS) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Eliminar historial",
                                color = if (hasScans) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            )
                        },
                        onClick = onRequestDeleteAll,
                        enabled = hasScans
                    )
                }
            }
        } else {
            Spacer(Modifier.width(48.dp))
        }
    }
}