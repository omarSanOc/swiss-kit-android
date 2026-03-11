package com.epic_engine.swisskit.feature.qrscanner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.feature.qrscanner.domain.model.CameraState
import com.epic_engine.swisskit.feature.qrscanner.domain.model.ScanMode
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.SaveQRScanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
            state.copy(scanMode = newMode, isScanning = true, lastScanResult = null)
        }
    }

    fun onResumeScanning() = _uiState.update { it.copy(isScanning = true) }

    fun onBarcodeDetected(content: String) {
        val now = System.currentTimeMillis()
        val state = _uiState.value

        if (!state.isScanning) return
        if (now - lastProcessedAt < cooldownMs) return
        lastProcessedAt = now

        if (state.scanMode == ScanMode.SINGLE) {
            _uiState.update { it.copy(isScanning = false) }
        }

        viewModelScope.launch {
            runCatching { saveQRScan(content) }
                .onSuccess { result ->
                    _uiState.update { it.copy(lastScanResult = result) }
                    _events.emit(QRCameraEvent.BarcodeDetected(content))
                }
                .onFailure {
                    _events.emit(QRCameraEvent.ShowError("Error al guardar el escaneo"))
                }
        }
    }
}
