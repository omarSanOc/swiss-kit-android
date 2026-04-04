package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.components.SwissKitSearchBar
import com.epic_engine.swisskit.core.designsystem.components.SwissKitToast
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.model.ScanMode
import com.epic_engine.swisskit.feature.qrscanner.presentation.theme.QRScannerDesignTokens
import com.epic_engine.swisskit.feature.qrscanner.presentation.util.QRCameraUiState

@Composable
fun ScannerTab(
    cameraUiState: QRCameraUiState,
    filteredScans: List<QRScan>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBarcodeDetected: (String) -> Unit,
    onResumeScanning: () -> Unit,
    onClearFeedback: () -> Unit,
    onCopyScan: (QRScan) -> Unit,
    onEditLabel: (QRScan) -> Unit,
    onOpenContent: (QRScan) -> Unit,
    onRequestDeleteScan: (QRScan) -> Unit,
    showDeleteScanConfirm: QRScan?,
    showDeleteAllConfirm: Boolean,
    onConfirmDeleteScan: () -> Unit,
    onRequestDeleteAll: () -> Unit,
    onConfirmDeleteAll: () -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Camera card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = QRScannerDesignTokens.screenHorizontalPadding)
                    .height(QRScannerDesignTokens.cameraCardHeight)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                CameraPreview(
                    isScanning = cameraUiState.isScanning,
                    onBarcodeDetected = onBarcodeDetected,
                    modifier = Modifier.fillMaxSize()
                )
                CameraCornerMarkers(modifier = Modifier.fillMaxSize())
            }

            // Search bar
            SwissKitSearchBar(
                tint = QRScannerDesignTokens.Primary,
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = QRScannerDesignTokens.screenHorizontalPadding,
                        vertical = 12.dp
                    ),
                description = "Buscar escaneos"
            )

            // History list
            HistorialTab(
                scans = filteredScans,
                showDeleteScanConfirm = showDeleteScanConfirm,
                showDeleteAllConfirm = showDeleteAllConfirm,
                onCopyScan = onCopyScan,
                onEditLabel = onEditLabel,
                onOpenContent = onOpenContent,
                onRequestDeleteScan = onRequestDeleteScan,
                onConfirmDeleteScan = onConfirmDeleteScan,
                onRequestDeleteAll = onRequestDeleteAll,
                onConfirmDeleteAll = onConfirmDeleteAll,
                onDismissDialog = onDismissDialog,
                modifier = Modifier.weight(1f)
            )
        }

        // Continuous mode feedback toast
        SwissKitToast(
            message = cameraUiState.feedbackMessage,
            onDismiss = onClearFeedback
        )

        // Resume FAB (single mode, after pausing)
        if (!cameraUiState.isScanning && cameraUiState.scanMode == ScanMode.SINGLE) {
            QRResumeFAB(
                onClick = onResumeScanning,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun CameraCornerMarkers(modifier: Modifier = Modifier) {
    val color = QRScannerDesignTokens.CornerMarker
    Canvas(modifier = modifier) {
        val strokeWidth = 4.dp.toPx()
        val cornerLen = 28.dp.toPx()
        val padding = 16.dp.toPx()

        val left = padding
        val top = padding
        val right = size.width - padding
        val bottom = size.height - padding

        val corners = listOf(
            Triple(left, top, 1f to 1f),
            Triple(right, top, -1f to 1f),
            Triple(left, bottom, 1f to -1f),
            Triple(right, bottom, -1f to -1f)
        )
        corners.forEach { (cx, cy, dir) ->
            val (dx, dy) = dir
            drawLine(color, Offset(cx, cy), Offset(cx + dx * cornerLen, cy), strokeWidth)
            drawLine(color, Offset(cx, cy), Offset(cx, cy + dy * cornerLen), strokeWidth)
        }
    }
}

@Composable
private fun QRResumeFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fabSize = 56.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessHigh),
        label = "fab_scale"
    )

    val pink = QRScannerDesignTokens.Primary
    Box(
        modifier = modifier
            .size(fabSize)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .clip(CircleShape)
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(pink, pink.copy(alpha = 0.8f)),
                        start = Offset.Zero,
                        end = Offset(0f, size.height)
                    )
                )
            }
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Reanudar escaneo",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
