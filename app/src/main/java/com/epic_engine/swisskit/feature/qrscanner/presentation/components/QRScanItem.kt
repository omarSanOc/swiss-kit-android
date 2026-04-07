package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
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


@Composable
fun QRScanItem(
    scan: QRScan,
    onCopy: () -> Unit,
    onEditLabel: () -> Unit,
    onOpenContent: () -> Unit,
    onRequestDelete: () -> Unit
) {
    val density = LocalDensity.current
    val isOpenable = scan.type in openableTypes
    val swipeWidthDp = if (isOpenable) QRScannerDesignTokens.swipeWidthThreeActions
                       else            QRScannerDesignTokens.swipeWidthTwoActions
    val actionButtonsWidthPx = with(density) { swipeWidthDp.toPx() }

    val onSurface        = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun closeSwipe() {
        scope.launch { offsetX.animateTo(0f, tween(250, easing = EaseInOut)) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTokens.dimensXXMedium))
            .testTag("qr_scan_item")
    ) {
        // Back layer: action buttons
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = DesignTokens.dimensXXXXSmall),
            horizontalArrangement = Arrangement.spacedBy(DesignTokens.dimensXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isOpenable) {
                IconButton(
                    onClick = {
                        closeSwipe()
                        onOpenContent()
                    },
                    modifier = Modifier
                        .size(QRScannerDesignTokens.dimensXLarge)
                        .clip(CircleShape)
                        .background(DesignTokens.actionColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Abrir",
                        tint = Color.White,
                        modifier = Modifier.size(DesignTokens.dimensXXMedium)
                    )
                }
            }

            IconButton(
                onClick = {
                    closeSwipe()
                    onEditLabel()
                },
                modifier = Modifier
                    .size(QRScannerDesignTokens.dimensXLarge)
                    .clip(CircleShape)
                    .background(DesignTokens.editColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar etiqueta",
                    tint = Color.White,
                    modifier = Modifier.size(DesignTokens.dimensXXMedium)
                )
            }

            IconButton(
                onClick = {
                    closeSwipe()
                    onRequestDelete()
                },
                modifier = Modifier
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
                            val newValue = (offsetX.value + delta).coerceIn(-actionButtonsWidthPx, 0f)
                            offsetX.snapTo(newValue)
                        }
                    },
                    onDragStopped = {
                        scope.launch {
                            val threshold = -actionButtonsWidthPx * 0.4f
                            if (offsetX.value < threshold) {
                                offsetX.animateTo(-actionButtonsWidthPx, tween(250, easing = EaseInOut))
                            } else {
                                offsetX.animateTo(0f, tween(250, easing = EaseInOut))
                            }
                        }
                    }
                )
                .clickable(onClick = onCopy),
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
    }
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}
