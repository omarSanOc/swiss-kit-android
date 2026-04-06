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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.presentation.theme.QRScannerDesignTokens
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


private val openableTypes = setOf(
    QRContentType.URL,
    QRContentType.EMAIL,
    QRContentType.PHONE,
    QRContentType.LOCATION
)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QRScanItem(
    scan: QRScan,
    onCopy: () -> Unit,
    onEditLabel: () -> Unit,
    onOpenContent: () -> Unit,
    onRequestDelete: () -> Unit
) {
    val density = LocalDensity.current
    val deleteButtonWidthPx = with(density) { DesignTokens.dimensXXLarge.toPx() }

    val onSurface        = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var menuExpanded by remember { mutableStateOf(false) }

    fun closeSwipe() {
        scope.launch { offsetX.animateTo(0f, tween(250, easing = EaseInOut)) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTokens.dimensXXMedium))
    ) {
        // Back layer: delete button
        IconButton(
            onClick = {
                closeSwipe()
                onRequestDelete()
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = DesignTokens.dimensXXMedium)
                .size(QRScannerDesignTokens.dimensXLarge)
                .clip(CircleShape)
                .background(DesignTokens.deleteColor)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = Color.White,
                modifier = Modifier.size(DesignTokens.dimensXXMedium)
            )
        }

        // Front layer: card
        SwissKitCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            scope.launch {
                                val newValue = (offsetX.value + delta).coerceIn(-deleteButtonWidthPx, 0f)
                                offsetX.snapTo(newValue)
                            }
                        },
                        onDragStopped = {
                            scope.launch {
                                val threshold = -deleteButtonWidthPx * 0.4f
                                if (offsetX.value < threshold) {
                                    offsetX.animateTo(-deleteButtonWidthPx, tween(250, easing = EaseInOut))
                                } else {
                                    offsetX.animateTo(0f, tween(250, easing = EaseInOut))
                                }
                            }
                        }
                    )
                    .combinedClickable(
                        onClick = onCopy,
                        onLongClick = { menuExpanded = true }
                    ),
            contentPadding = PaddingValues(DesignTokens.dimensXSmall)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignTokens.dimensSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_link),
                        contentDescription = null,
                        modifier = Modifier.size(DesignTokens.dimensXXXMedium),
                        tint = QRScannerDesignTokens.Primary
                    )
                    Spacer(Modifier.width(DesignTokens.dimensSmall))
                    Column(modifier = Modifier.weight(1f)) {
                        if (scan.label.isNotBlank()) {
                            Text(
                                text = scan.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = scan.content,
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formatDate(scan.scannedAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Long-press context menu
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Editar etiqueta") },
                    onClick = { menuExpanded = false; onEditLabel() }
                )
                if (scan.type in openableTypes) {
                    DropdownMenuItem(
                        text = { Text("Abrir") },
                        onClick = { menuExpanded = false; onOpenContent() }
                    )
                }
            }
    }
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}