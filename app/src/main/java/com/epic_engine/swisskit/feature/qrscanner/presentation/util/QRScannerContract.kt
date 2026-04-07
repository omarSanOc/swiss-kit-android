package com.epic_engine.swisskit.feature.qrscanner.presentation.util

import android.graphics.Bitmap
import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.feature.qrscanner.domain.model.CameraState
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.model.ScanMode

data class PendingQRResult(
    val content: String,
    val type: QRContentType
)

data class QRCameraUiState(
    val cameraState: CameraState = CameraState.Checking,
    val scanMode: ScanMode = ScanMode.SINGLE,
    val pendingResult: PendingQRResult? = null,
    val feedbackMessage: UiText? = null,
    val isScanning: Boolean = true
)

sealed interface QRCameraEvent {
    data class ShowError(val message: UiText) : QRCameraEvent
}

data class QRScannerUiState(
    val scans: List<QRScan> = emptyList(),
    val filteredScans: List<QRScan> = emptyList(),
    val isLoading: Boolean = false,
    val generatorInput: String = "",
    val generatedBitmap: Bitmap? = null,
    val isGenerating: Boolean = false,
    val searchQuery: String = "",
    val editingLabelScan: QRScan? = null,
    val editLabelDraft: String = "",
    val showDeleteAllConfirm: Boolean = false,
    val showDeleteScanConfirm: QRScan? = null
)

sealed interface QRScannerEvent {
    data class ShowError(val message: UiText) : QRScannerEvent
    data class ShareQR(val bitmap: Bitmap) : QRScannerEvent
    data object QRSavedToGallery : QRScannerEvent
    data object AllScansDeleted : QRScannerEvent
}
