package com.epic_engine.swisskit.feature.qrscanner.presentation

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitBackground
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.core.designsystem.components.SwissKitToast
import com.epic_engine.swisskit.feature.qrscanner.domain.detector.QRContentDetector
import com.epic_engine.swisskit.feature.qrscanner.domain.model.CameraState
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.ScanMode
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.GeneratorTab
import com.epic_engine.swisskit.core.designsystem.components.SwissKitTabPicker
import com.epic_engine.swisskit.feature.home.presentation.theme.HomeDesignTokens
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.CameraPermissionDeniedScreen
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.QRHeader
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.ScanResultBottomSheet
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.ScannerTab
import com.epic_engine.swisskit.feature.qrscanner.presentation.theme.QRScannerDesignTokens
import com.epic_engine.swisskit.feature.qrscanner.presentation.util.QRCameraEvent
import com.epic_engine.swisskit.feature.qrscanner.presentation.util.QRScannerEvent
import com.epic_engine.swisskit.feature.qrscanner.presentation.viewmodel.QRCameraViewModel
import com.epic_engine.swisskit.feature.qrscanner.presentation.viewmodel.QRScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
    qrCameraViewModel: QRCameraViewModel = hiltViewModel(),
    qrScannerViewModel: QRScannerViewModel = hiltViewModel()
) {
    val cameraUiState by qrCameraViewModel.uiState.collectAsStateWithLifecycle()
    val scannerUiState by qrScannerViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val generatorScrollState = rememberScrollState()
    var selectedIndex by remember { mutableIntStateOf(0) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var screenToastMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) qrCameraViewModel.onCameraPermissionGranted()
        else qrCameraViewModel.onCameraPermissionDenied()
    }

    LaunchedEffect(Unit) {
        val permission = android.Manifest.permission.CAMERA
        when (ContextCompat.checkSelfPermission(context, permission)) {
            PackageManager.PERMISSION_GRANTED -> qrCameraViewModel.onCameraPermissionGranted()
            else -> permissionLauncher.launch(permission)
        }
    }

    LaunchedEffect(Unit) {
        qrCameraViewModel.events.collect { event ->
            when (event) {
                is QRCameraEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(Unit) {
        qrScannerViewModel.events.collect { event ->
            when (event) {
                is QRScannerEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                QRScannerEvent.QRSavedToGallery -> screenToastMessage = "Imagen guardada en galería"
                QRScannerEvent.AllScansDeleted -> { /* handled via state */ }
                is QRScannerEvent.ShareQR -> shareQRBitmap(context, event.bitmap)
            }
        }
    }

    SwissKitBackground(
        colors = listOf(QRScannerDesignTokens.Primary, QRScannerDesignTokens.background),
        darkColors = listOf(QRScannerDesignTokens.Primary, QRScannerDesignTokens.darkBackground),
        content = {
        Box(modifier =
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        QRHeader(
                            selectedIndex = selectedIndex,
                            scanMode = cameraUiState.scanMode,
                            hasScans = scannerUiState.scans.isNotEmpty(),
                            showOverflowMenu = showOverflowMenu,
                            onOpenOverflow = { showOverflowMenu = true },
                            onDismissOverflow = { showOverflowMenu = false },
                            onSetScanMode = { mode ->
                                qrCameraViewModel.setScanMode(mode)
                                showOverflowMenu = false
                            },
                            onRequestDeleteAll = {
                                qrScannerViewModel.onRequestDeleteAll()
                                showOverflowMenu = false
                            }
                        )
                        SwissKitTabPicker(
                            options = listOf("Escáner", "Generador"),
                            selectedIndex = selectedIndex,
                            onTabSelected = { selectedIndex = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = DesignTokens.dimensXXXMedium,
                                    vertical = DesignTokens.dimensXSmall
                                )
                        )
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { padding ->
                if (selectedIndex == 1) {
                    // Generador: Column scrollable dentro del área de Scaffold
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(generatorScrollState)
                            .padding(padding)
                            .imePadding()
                            .padding(bottom = QRScannerDesignTokens.dimensXXLarge)
                    ) {
                        GeneratorTab(
                            uiState = scannerUiState,
                            onInputChange = qrScannerViewModel::onGeneratorInputChange,
                            onGenerate = qrScannerViewModel::onGenerateQR,
                            onShare = qrScannerViewModel::onShareQR,
                            onSaveToGallery = { qrScannerViewModel.onSaveQRToGallery(context) }
                        )
                    }
                } else {
                    // Escáner: Box que llena el espacio restante bajo el header fijo
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        when (cameraUiState.cameraState) {
                            CameraState.Checking -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = QRScannerDesignTokens.Primary)
                                }
                            }
                            CameraState.Denied -> {
                                CameraPermissionDeniedScreen(
                                    onOpenSettings = {
                                        val intent = android.content.Intent(
                                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            android.net.Uri.fromParts("package", context.packageName, null)
                                        )
                                        context.startActivity(intent)
                                    }
                                )
                            }
                            CameraState.Unavailable -> {
                                SwissKitEmptyView(
                                    title = "Cámara no disponible",
                                    subtitle = "Este dispositivo no tiene cámara compatible",
                                    iconTint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            CameraState.Authorized -> {
                                ScannerTab(
                                    cameraUiState = cameraUiState,
                                    filteredScans = scannerUiState.filteredScans,
                                    searchQuery = scannerUiState.searchQuery,
                                    onSearchQueryChange = qrScannerViewModel::onSearchQueryChange,
                                    onBarcodeDetected = qrCameraViewModel::onBarcodeDetected,
                                    onResumeScanning = qrCameraViewModel::onResumeScanning,
                                    onClearFeedback = qrCameraViewModel::onClearFeedback,
                                    onCopyScan = { scan ->
                                        clipboardManager.setText(AnnotatedString(scan.content))
                                        screenToastMessage = "Copiado"
                                    },
                                    onEditLabel = qrScannerViewModel::onEditLabel,
                                    onOpenContent = { scan -> openContent(context, scan.content, scan.type) },
                                    onRequestDeleteScan = qrScannerViewModel::onRequestDeleteScan,
                                    showDeleteScanConfirm = scannerUiState.showDeleteScanConfirm,
                                    showDeleteAllConfirm = scannerUiState.showDeleteAllConfirm,
                                    onConfirmDeleteScan = qrScannerViewModel::onConfirmDeleteScan,
                                    onRequestDeleteAll = qrScannerViewModel::onRequestDeleteAll,
                                    onConfirmDeleteAll = qrScannerViewModel::onConfirmDeleteAll,
                                    onDismissDialog = qrScannerViewModel::onDismissDialog
                                )
                            }
                        }
                    }
                }
            }
        }

        // Toast como overlay sobre el Scaffold (z-order por ser segundo hijo del Box de SwissKitBackground)
        SwissKitToast(message = screenToastMessage, onDismiss = { screenToastMessage = null })
    })

    // Single mode result bottom sheet
    cameraUiState.pendingResult?.let { pending ->
        val initialLabel = remember(pending.content) {
            QRContentDetector.generateLabel(pending.content, pending.type)
        }
        ScanResultBottomSheet(
            pendingResult = pending,
            initialLabel = initialLabel,
            onSave = { content, label -> qrCameraViewModel.onSaveResult(content, label) },
            onDismiss = qrCameraViewModel::onDismissResult,
            onOpenContent = { result -> openContent(context, result.content, result.type) }
        )
    }

    // Edit label dialog
    scannerUiState.editingLabelScan?.let {
        AlertDialog(
            onDismissRequest = qrScannerViewModel::onDismissEditLabel,
            title = { Text("Editar etiqueta") },
            text = {
                OutlinedTextField(
                    value = scannerUiState.editLabelDraft,
                    onValueChange = qrScannerViewModel::onEditLabelDraftChange,
                    label = { Text("Etiqueta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = qrScannerViewModel::onConfirmEditLabel) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = qrScannerViewModel::onDismissEditLabel) { Text("Cancelar") }
            }
        )
    }
}




private fun shareQRBitmap(context: android.content.Context, bitmap: Bitmap) {
    runCatching {
        val file = java.io.File(context.cacheDir, "qr_share_${System.currentTimeMillis()}.png")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context, "${context.packageName}.provider", file
        )
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Compartir QR"))
    }
}

private fun openContent(
    context: android.content.Context,
    content: String,
    type: QRContentType
) {
    val intent = when (type) {
        QRContentType.URL, QRContentType.LOCATION ->
            android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(content))
        QRContentType.PHONE ->
            android.content.Intent(
                android.content.Intent.ACTION_DIAL,
                android.net.Uri.parse(if (content.startsWith("tel:")) content else "tel:$content")
            )
        QRContentType.EMAIL ->
            android.content.Intent(
                android.content.Intent.ACTION_SENDTO,
                android.net.Uri.parse(if (content.startsWith("mailto:")) content else "mailto:$content")
            )
        else ->
            android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(content))
    }
    runCatching { context.startActivity(intent) }
}
