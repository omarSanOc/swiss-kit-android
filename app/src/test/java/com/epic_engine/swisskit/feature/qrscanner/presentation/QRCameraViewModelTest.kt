package com.epic_engine.swisskit.feature.qrscanner.presentation

import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScanSaveResult
import com.epic_engine.swisskit.feature.qrscanner.domain.model.ScanMode
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.SaveQRScanUseCase
import com.epic_engine.swisskit.feature.qrscanner.presentation.viewmodel.QRCameraViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QRCameraViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var saveQRScan: SaveQRScanUseCase
    private lateinit var viewModel: QRCameraViewModel

    private val fakeScan = QRScan(
        id = "1",
        content = "https://example.com",
        type = QRContentType.URL,
        label = "Enlace: example.com",
        scannedAt = System.currentTimeMillis()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        saveQRScan = mockk()
        viewModel = QRCameraViewModel(saveQRScan)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `single mode barcode detection pauses scanning and exposes pendingResult without saving`() = runTest {
        viewModel.onCameraPermissionGranted()
        assertTrue(viewModel.uiState.value.isScanning)

        viewModel.onBarcodeDetected("https://example.com")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isScanning)
        assertNotNull(viewModel.uiState.value.pendingResult)
        assertEquals(QRContentType.URL, viewModel.uiState.value.pendingResult?.type)
        coVerify(exactly = 0) { saveQRScan(any(), any()) }
    }

    @Test
    fun `continuous mode barcode detection auto-saves and sets feedbackMessage`() = runTest {
        coEvery { saveQRScan(any(), any()) } returns QRScanSaveResult.Created(fakeScan)

        viewModel.setScanMode(ScanMode.CONTINUOUS)
        viewModel.onCameraPermissionGranted()

        viewModel.onBarcodeDetected("https://example.com")
        advanceUntilIdle()

        coVerify(exactly = 1) { saveQRScan(any(), any()) }
        assertNotNull(viewModel.uiState.value.feedbackMessage)
    }

    @Test
    fun `onResumeScanning sets isScanning true and clears pendingResult`() = runTest {
        // First pause via single mode detection
        viewModel.onBarcodeDetected("https://example.com")
        assertFalse(viewModel.uiState.value.isScanning)
        assertNotNull(viewModel.uiState.value.pendingResult)

        viewModel.onResumeScanning()

        assertTrue(viewModel.uiState.value.isScanning)
        assertNull(viewModel.uiState.value.pendingResult)
    }
}
