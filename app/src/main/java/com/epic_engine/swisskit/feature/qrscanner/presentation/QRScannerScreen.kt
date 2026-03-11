package com.epic_engine.swisskit.feature.qrscanner.presentation

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.feature.qrscanner.domain.model.CameraState
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.GeneratorTab
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.HistorialTab
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.QREmptyState
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.ScannerTab

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
                is QRCameraEvent.BarcodeDetected -> { /* estado ya actualizado */ }
            }
        }
    }

    LaunchedEffect(Unit) {
        qrScannerViewModel.events.collect { event ->
            when (event) {
                is QRScannerEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                QRScannerEvent.QRSavedToGallery -> snackbarHostState.showSnackbar("Imagen guardada en galería")
                QRScannerEvent.AllScansDeleted -> snackbarHostState.showSnackbar("Historial eliminado")
                is QRScannerEvent.ShareQR -> shareQRBitmap(context, event.bitmap)
            }
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Escáner", "Generar", "Historial")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("QR Scanner") },
                    actions = {
                        if (selectedTabIndex == 2 && scannerUiState.scans.isNotEmpty()) {
                            IconButton(onClick = qrScannerViewModel::onDeleteAllScans) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = "Eliminar historial")
                            }
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = QRScannerDesignTokens.Primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTabIndex) {
                0 -> when (cameraUiState.cameraState) {
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
                        QREmptyState(message = "Cámara no disponible en este dispositivo")
                    }
                    CameraState.Authorized -> {
                        ScannerTab(
                            uiState = cameraUiState,
                            onBarcodeDetected = qrCameraViewModel::onBarcodeDetected,
                            onToggleMode = qrCameraViewModel::onToggleScanMode,
                            onResumeScanning = qrCameraViewModel::onResumeScanning
                        )
                    }
                }
                1 -> GeneratorTab(
                    uiState = scannerUiState,
                    onInputChange = qrScannerViewModel::onGeneratorInputChange,
                    onGenerate = qrScannerViewModel::onGenerateQR,
                    onShare = qrScannerViewModel::onShareQR,
                    onSaveToGallery = { qrScannerViewModel.onSaveQRToGallery(context) }
                )
                2 -> HistorialTab(
                    scans = scannerUiState.scans,
                    onDeleteScan = qrScannerViewModel::onDeleteScan,
                    onCopyContent = { content ->
                        clipboardManager.setText(AnnotatedString(content))
                    },
                    onOpenContent = { scan -> openQRContent(context, scan) }
                )
            }
        }
    }
}

@Composable
private fun CameraPermissionDeniedScreen(onOpenSettings: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "Se necesita permiso de cámara para escanear códigos QR",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = onOpenSettings,
                colors = ButtonDefaults.buttonColors(containerColor = QRScannerDesignTokens.Primary)
            ) {
                Text("Ir a Ajustes")
            }
        }
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

private fun openQRContent(context: android.content.Context, scan: QRScan) {
    val intent = when (scan.type) {
        QRContentType.URL, QRContentType.LOCATION ->
            android.content.Intent(android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse(scan.content))
        QRContentType.PHONE ->
            android.content.Intent(android.content.Intent.ACTION_DIAL,
                android.net.Uri.parse(if (scan.content.startsWith("tel:")) scan.content else "tel:${scan.content}"))
        QRContentType.EMAIL ->
            android.content.Intent(android.content.Intent.ACTION_SENDTO,
                android.net.Uri.parse(if (scan.content.startsWith("mailto:")) scan.content else "mailto:${scan.content}"))
        else ->
            android.content.Intent(android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse(scan.content))
    }
    runCatching { context.startActivity(intent) }
}
