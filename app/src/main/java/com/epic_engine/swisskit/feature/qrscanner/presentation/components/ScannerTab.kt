package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScanSaveResult
import com.epic_engine.swisskit.feature.qrscanner.domain.model.ScanMode
import com.epic_engine.swisskit.feature.qrscanner.presentation.QRCameraUiState
import com.epic_engine.swisskit.feature.qrscanner.presentation.QRScannerDesignTokens

@Composable
fun ScannerTab(
    uiState: QRCameraUiState,
    onBarcodeDetected: (String) -> Unit,
    onToggleMode: () -> Unit,
    onResumeScanning: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(
            isScanning = uiState.isScanning,
            onBarcodeDetected = onBarcodeDetected,
            modifier = Modifier.fillMaxSize()
        )

        ScannerOverlay(modifier = Modifier.fillMaxSize())

        uiState.lastScanResult?.let { result ->
            val message = when (result) {
                is QRScanSaveResult.Created -> "✓ Guardado: ${result.scan.label}"
                is QRScanSaveResult.MergedDuplicate -> "↻ Actualizado: ${result.scan.label}"
                QRScanSaveResult.Failed -> "✗ Error al guardar"
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (result is QRScanSaveResult.Failed)
                    MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!uiState.isScanning && uiState.scanMode == ScanMode.SINGLE) {
                Button(
                    onClick = onResumeScanning,
                    colors = ButtonDefaults.buttonColors(containerColor = QRScannerDesignTokens.Primary)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Escanear de nuevo")
                }
            }

            FilterChip(
                selected = uiState.scanMode == ScanMode.CONTINUOUS,
                onClick = onToggleMode,
                label = {
                    Text(if (uiState.scanMode == ScanMode.SINGLE) "Modo: Una vez" else "Modo: Continuo")
                },
                leadingIcon = {
                    Icon(
                        if (uiState.scanMode == ScanMode.SINGLE) Icons.Default.CropFree
                        else Icons.Default.AllInclusive,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun ScannerOverlay(modifier: Modifier = Modifier) {
    val overlayColor = QRScannerDesignTokens.ScannerOverlay
    val frameColor = QRScannerDesignTokens.ScannerFrame

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val frameSize = 280.dp.toPx()
        val frameLeft = (canvasWidth - frameSize) / 2f
        val frameTop = (canvasHeight - frameSize) / 2f

        // Overlay semitransparente
        drawRect(color = overlayColor)

        // Recorte transparente central
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(frameLeft, frameTop),
            size = Size(frameSize, frameSize),
            cornerRadius = CornerRadius(12.dp.toPx()),
            blendMode = BlendMode.Clear
        )

        // Marco decorativo
        val strokeWidth = 4.dp.toPx()
        val cornerLength = 36.dp.toPx()
        val cr = 12.dp.toPx()

        drawRoundRect(
            color = frameColor,
            topLeft = Offset(frameLeft, frameTop),
            size = Size(frameSize, frameSize),
            cornerRadius = CornerRadius(cr),
            style = Stroke(width = strokeWidth)
        )

        // Esquinas en naranja más gruesas
        val corners = listOf(
            Offset(frameLeft, frameTop),
            Offset(frameLeft + frameSize - cornerLength, frameTop),
            Offset(frameLeft, frameTop + frameSize - cornerLength),
            Offset(frameLeft + frameSize - cornerLength, frameTop + frameSize - cornerLength)
        )
        corners.forEach { corner ->
            drawRect(
                color = frameColor,
                topLeft = corner,
                size = Size(cornerLength, strokeWidth * 2)
            )
            drawRect(
                color = frameColor,
                topLeft = corner,
                size = Size(strokeWidth * 2, cornerLength)
            )
        }
    }
}
