package com.epic_engine.swisskit.feature.qrscanner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.feature.qrscanner.domain.detector.QRContentDetector
import com.epic_engine.swisskit.feature.qrscanner.domain.model.CameraState
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScanSaveResult
import com.epic_engine.swisskit.feature.qrscanner.domain.model.ScanMode
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.SaveQRScanUseCase
import com.epic_engine.swisskit.feature.qrscanner.presentation.util.PendingQRResult
import com.epic_engine.swisskit.feature.qrscanner.presentation.util.QRCameraEvent
import com.epic_engine.swisskit.feature.qrscanner.presentation.util.QRCameraUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRCameraViewModel @Inject constructor(
    private val saveQRScan: SaveQRScanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRCameraUiState())
    val uiState: StateFlow<QRCameraUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<QRCameraEvent>()
    val events: SharedFlow<QRCameraEvent> = _events.asSharedFlow()

    private var lastProcessedAt = 0L
    private val cooldownMs = 2_000L

    fun onCameraPermissionGranted() =
        _uiState.update { it.copy(cameraState = CameraState.Authorized) }

    fun onCameraPermissionDenied() =
        _uiState.update { it.copy(cameraState = CameraState.Denied) }

    fun onCameraUnavailable() =
        _uiState.update { it.copy(cameraState = CameraState.Unavailable) }

    fun onToggleScanMode() {
        _uiState.update { state ->
            val newMode = if (state.scanMode == ScanMode.SINGLE) ScanMode.CONTINUOUS else ScanMode.SINGLE
            state.copy(scanMode = newMode, isScanning = true, pendingResult = null)
        }
    }

    fun setScanMode(mode: ScanMode) {
        _uiState.update { it.copy(scanMode = mode, isScanning = true, pendingResult = null) }
    }

    fun onResumeScanning() =
        _uiState.update { it.copy(isScanning = true, pendingResult = null) }

    fun onBarcodeDetected(content: String) {
        val now = System.currentTimeMillis()
        val state = _uiState.value

        if (!state.isScanning) return
        if (now - lastProcessedAt < cooldownMs) return
        lastProcessedAt = now

        if (state.scanMode == ScanMode.SINGLE) {
            val type = QRContentDetector.detect(content)
            _uiState.update { it.copy(isScanning = false, pendingResult = PendingQRResult(
                content,
                type
            )
            ) }
        } else {
            viewModelScope.launch {
                runCatching { saveQRScan(content) }
                    .onSuccess { result ->
                        val msg = when (result) {
                            is QRScanSaveResult.Created -> "Guardado: ${result.scan.label}"
                            is QRScanSaveResult.MergedDuplicate -> "Actualizado: ${result.scan.label}"
                            QRScanSaveResult.Failed -> "Error al guardar"
                        }
                        _uiState.update { it.copy(feedbackMessage = msg) }
                        delay(2_000)
                        _uiState.update { it.copy(feedbackMessage = null) }
                    }
                    .onFailure {
                        _events.emit(QRCameraEvent.ShowError("Error al guardar el escaneo"))
                    }
            }
        }
    }

    fun onSaveResult(content: String, label: String) {
        viewModelScope.launch {
            runCatching { saveQRScan(content, label) }
                .onFailure { _events.emit(QRCameraEvent.ShowError("Error al guardar el escaneo")) }
            _uiState.update { it.copy(pendingResult = null, isScanning = true) }
        }
    }

    fun onDismissResult() =
        _uiState.update { it.copy(pendingResult = null, isScanning = true) }

    fun onClearFeedback() =
        _uiState.update { it.copy(feedbackMessage = null) }
}
