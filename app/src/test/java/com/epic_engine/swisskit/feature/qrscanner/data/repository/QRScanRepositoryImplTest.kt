package com.epic_engine.swisskit.feature.qrscanner.data.repository

import com.epic_engine.swisskit.feature.qrscanner.data.local.QRScanDao
import com.epic_engine.swisskit.feature.qrscanner.data.local.QRScanEntity
import com.epic_engine.swisskit.feature.qrscanner.domain.detector.QRContentDetector
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScanSaveResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QRScanRepositoryImplTest {

    private lateinit var dao: QRScanDao
    private lateinit var repository: QRScanRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk()
        coEvery { dao.observeAll() } returns flowOf(emptyList())
        repository = QRScanRepositoryImpl(dao)
    }

    @Test
    fun `save with empty label generates label from detector`() = runTest {
        val content = "https://example.com"
        val expectedType = QRContentDetector.detect(content)
        val expectedLabel = QRContentDetector.generateLabel(content, expectedType)

        coEvery { dao.findDuplicate(any(), any()) } returns null
        val insertedEntity = slot<QRScanEntity>()
        coEvery { dao.insert(capture(insertedEntity)) } just Runs

        val result = repository.save(content, "")

        assertTrue(result is QRScanSaveResult.Created)
        assertEquals(expectedLabel, insertedEntity.captured.label)
    }

    @Test
    fun `save with explicit label stores that label`() = runTest {
        val content = "https://example.com"
        val customLabel = "Mi enlace favorito"

        coEvery { dao.findDuplicate(any(), any()) } returns null
        val insertedEntity = slot<QRScanEntity>()
        coEvery { dao.insert(capture(insertedEntity)) } just Runs

        val result = repository.save(content, customLabel)

        assertTrue(result is QRScanSaveResult.Created)
        assertEquals(customLabel, insertedEntity.captured.label)
    }

    @Test
    fun `save duplicate with empty label preserves existing label`() = runTest {
        val content = "https://example.com"
        val existingLabel = "Etiqueta original"
        val existingEntity = QRScanEntity(
            id = "existing-id",
            content = content.trim().lowercase(),
            type = QRContentType.URL.name,
            label = existingLabel,
            scannedAt = 1000L
        )

        coEvery { dao.findDuplicate(any(), any()) } returns existingEntity
        val updatedEntity = slot<QRScanEntity>()
        coEvery { dao.insert(capture(updatedEntity)) } just Runs

        val result = repository.save(content, "")

        assertTrue(result is QRScanSaveResult.MergedDuplicate)
        assertEquals(existingLabel, updatedEntity.captured.label)
    }

    @Test
    fun `updateLabel delegates to dao`() = runTest {
        coEvery { dao.updateLabel(any(), any()) } just Runs

        repository.updateLabel("test-id", "nueva etiqueta")

        coVerify(exactly = 1) { dao.updateLabel("test-id", "nueva etiqueta") }
    }
}
