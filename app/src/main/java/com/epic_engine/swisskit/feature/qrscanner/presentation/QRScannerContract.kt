package com.epic_engine.swisskit.feature.qrscanner.presentation

import android.graphics.Bitmap
import com.epic_engine.swisskit.feature.qrscanner.domain.model.CameraState
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScanSaveResult
import com.epic_engine.swisskit.feature.qrscanner.domain.model.ScanMode

data class QRCameraUiState(
    val cameraState: CameraState = CameraState.Checking,
    val scanMode: ScanMode = ScanMode.SINGLE,
    val lastScanResult: QRScanSaveResult? = null,
    val isScanning: Boolean = true
)

sealed interface QRCameraEvent {
    data class BarcodeDetected(val content: String) : QRCameraEvent
    data class ShowError(val message: String) : QRCameraEvent
}

data class QRScannerUiState(
    val scans: List<QRScan> = emptyList(),
    val isLoading: Boolean = false,
    val generatorInput: String = "",
    val generatedBitmap: Bitmap? = null,
    val isGenerating: Boolean = false
)

sealed interface QRScannerEvent {
    data class ShowError(val message: String) : QRScannerEvent
    data class ShareQR(val bitmap: Bitmap) : QRScannerEvent
    data object QRSavedToGallery : QRScannerEvent
    data object AllScansDeleted : QRScannerEvent
}
