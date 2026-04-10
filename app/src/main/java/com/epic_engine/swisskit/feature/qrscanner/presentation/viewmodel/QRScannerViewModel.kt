package com.epic_engine.swisskit.feature.qrscanner.presentation.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.DeleteAllQRScansUseCase
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.DeleteQRScanUseCase
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.GenerateQRBitmapUseCase
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.ObserveQRScansUseCase
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.UpdateQRScanLabelUseCase
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.feature.qrscanner.presentation.util.QRScannerEvent
import com.epic_engine.swisskit.feature.qrscanner.presentation.util.QRScannerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QRScannerViewModel @Inject constructor(
    private val observeScans: ObserveQRScansUseCase,
    private val deleteScan: DeleteQRScanUseCase,
    private val deleteAllScans: DeleteAllQRScansUseCase,
    private val generateQRBitmap: GenerateQRBitmapUseCase,
    private val updateLabel: UpdateQRScanLabelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QRScannerUiState())
    val uiState: StateFlow<QRScannerUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<QRScannerEvent>(replay = 1, extraBufferCapacity = 1)
    val events: SharedFlow<QRScannerEvent> = _events.asSharedFlow()

    init {
        observeScans()
            .onEach { scans ->
                _uiState.update { state ->
                    state.copy(
                        scans = scans,
                        filteredScans = filterScans(scans, state.searchQuery)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredScans = filterScans(state.scans, query)
            )
        }
    }

    fun onGeneratorInputChange(input: String) {
        _uiState.update { it.copy(generatorInput = input, generatedBitmap = null) }
    }

    fun onGenerateQR() {
        val input = _uiState.value.generatorInput.trim()
        if (input.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            val bitmap = generateQRBitmap(input)
            _uiState.update { it.copy(generatedBitmap = bitmap, isGenerating = false) }
        }
    }

    fun onShareQR() {
        val bitmap = _uiState.value.generatedBitmap ?: return
        viewModelScope.launch { _events.emit(QRScannerEvent.ShareQR(bitmap)) }
    }

    fun onSaveQRToGallery(context: Context) {
        val bitmap = _uiState.value.generatedBitmap ?: return
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { saveBitmapToMediaStore(context, bitmap) }
            }.onSuccess {
                _events.emit(QRScannerEvent.QRSavedToGallery)
            }.onFailure {
                _events.emit(QRScannerEvent.ShowError(UiText.StringRes(R.string.qr_error_save_image)))
            }
        }
    }

    fun onEditLabel(scan: QRScan) {
        _uiState.update { it.copy(editingLabelScan = scan, editLabelDraft = scan.label) }
    }

    fun onEditLabelDraftChange(draft: String) {
        _uiState.update { it.copy(editLabelDraft = draft) }
    }

    fun onConfirmEditLabel() {
        val scan = _uiState.value.editingLabelScan ?: return
        val draft = _uiState.value.editLabelDraft
        viewModelScope.launch {
            runCatching { updateLabel(scan.id, draft) }
                .onFailure { _events.emit(QRScannerEvent.ShowError(UiText.StringRes(R.string.qr_error_update_label))) }
            _uiState.update { it.copy(editingLabelScan = null, editLabelDraft = "") }
        }
    }

    fun onDismissEditLabel() {
        _uiState.update { it.copy(editingLabelScan = null, editLabelDraft = "") }
    }

    fun onRequestDeleteScan(scan: QRScan) {
        _uiState.update { it.copy(showDeleteScanConfirm = scan) }
    }

    fun onConfirmDeleteScan() {
        val scan = _uiState.value.showDeleteScanConfirm ?: return
        viewModelScope.launch {
            runCatching { deleteScan(scan) }
                .onFailure { _events.emit(QRScannerEvent.ShowError(UiText.StringRes(R.string.common_error))) }
            _uiState.update { it.copy(showDeleteScanConfirm = null) }
        }
    }

    fun onRequestDeleteAll() {
        _uiState.update { it.copy(showDeleteAllConfirm = true) }
    }

    fun onConfirmDeleteAll() {
        viewModelScope.launch {
            runCatching { deleteAllScans() }
                .onSuccess { _events.emit(QRScannerEvent.AllScansDeleted) }
                .onFailure { _events.emit(QRScannerEvent.ShowError(UiText.StringRes(R.string.common_error))) }
            _uiState.update { it.copy(showDeleteAllConfirm = false) }
        }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(showDeleteAllConfirm = false, showDeleteScanConfirm = null) }
    }

    private fun filterScans(scans: List<QRScan>, query: String): List<QRScan> {
        if (query.isBlank()) return scans
        return scans.filter {
            it.content.contains(query, ignoreCase = true) ||
                it.label.contains(query, ignoreCase = true)
        }
    }

    private fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "QR_SwissKit_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/SwissKit")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        ) ?: throw Exception("No se pudo crear URI en MediaStore")

        context.contentResolver.openOutputStream(uri)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
        }
    }
}
