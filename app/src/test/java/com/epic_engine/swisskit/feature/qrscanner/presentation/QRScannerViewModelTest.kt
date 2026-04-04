package com.epic_engine.swisskit.feature.qrscanner.presentation

import android.graphics.Bitmap
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.DeleteAllQRScansUseCase
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.DeleteQRScanUseCase
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.GenerateQRBitmapUseCase
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.ObserveQRScansUseCase
import com.epic_engine.swisskit.feature.qrscanner.domain.usecase.UpdateQRScanLabelUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
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
class QRScannerViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var observeScans: ObserveQRScansUseCase
    private lateinit var deleteScan: DeleteQRScanUseCase
    private lateinit var deleteAllScans: DeleteAllQRScansUseCase
    private lateinit var generateQRBitmap: GenerateQRBitmapUseCase
    private lateinit var updateLabel: UpdateQRScanLabelUseCase

    private val urlScan = QRScan("1", "https://example.com", QRContentType.URL, "Enlace: example.com", 1000L)
    private val textScan = QRScan("2", "Texto plano", QRContentType.TEXT, "Texto plano", 2000L)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        observeScans = mockk()
        deleteScan = mockk()
        deleteAllScans = mockk()
        generateQRBitmap = mockk()
        updateLabel = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(): QRScannerViewModel {
        coEvery { observeScans() } returns flowOf(listOf(urlScan, textScan))
        return QRScannerViewModel(observeScans, deleteScan, deleteAllScans, generateQRBitmap, updateLabel)
    }

    @Test
    fun `search query filters scans by content and label`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.filteredScans.size)

        viewModel.onSearchQueryChange("example")
        assertEquals(1, viewModel.uiState.value.filteredScans.size)
        assertEquals(urlScan.id, viewModel.uiState.value.filteredScans.first().id)
    }

    @Test
    fun `onConfirmEditLabel calls updateLabel use case and clears editing state`() = runTest {
        coEvery { updateLabel(any(), any()) } just Runs
        val viewModel = buildViewModel()

        viewModel.onEditLabel(urlScan)
        assertEquals(urlScan, viewModel.uiState.value.editingLabelScan)

        viewModel.onEditLabelDraftChange("nueva etiqueta")
        viewModel.onConfirmEditLabel()
        advanceUntilIdle()

        coVerify(exactly = 1) { updateLabel(urlScan.id, "nueva etiqueta") }
        assertNull(viewModel.uiState.value.editingLabelScan)
    }

    @Test
    fun `onGeneratorInputChange clears generatedBitmap`() = runTest {
        val fakeBitmap = mockk<Bitmap>(relaxed = true)
        coEvery { generateQRBitmap(any()) } returns fakeBitmap
        val viewModel = buildViewModel()

        viewModel.onGeneratorInputChange("https://example.com")
        viewModel.onGenerateQR()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.generatedBitmap)

        viewModel.onGeneratorInputChange("nuevo texto")
        assertNull(viewModel.uiState.value.generatedBitmap)
    }

    @Test
    fun `onConfirmDeleteAll calls use case and emits AllScansDeleted event`() = runTest {
        coEvery { deleteAllScans() } just Runs
        val viewModel = buildViewModel()

        val emittedEvents = mutableListOf<QRScannerEvent>()
        val job = launch { viewModel.events.toList(emittedEvents) }

        viewModel.onRequestDeleteAll()
        assertTrue(viewModel.uiState.value.showDeleteAllConfirm)

        viewModel.onConfirmDeleteAll()
        advanceUntilIdle()

        coVerify(exactly = 1) { deleteAllScans() }
        assertFalse(viewModel.uiState.value.showDeleteAllConfirm)
        assertTrue(emittedEvents.any { it is QRScannerEvent.AllScansDeleted })

        job.cancel()
    }
}
