package com.epic_engine.swisskit.feature.qrscanner.data.repository

import com.epic_engine.swisskit.feature.qrscanner.data.local.QRScanDao
import com.epic_engine.swisskit.feature.qrscanner.data.local.QRScanEntity
import com.epic_engine.swisskit.feature.qrscanner.data.mapper.toDomain
import com.epic_engine.swisskit.feature.qrscanner.data.mapper.toEntity
import com.epic_engine.swisskit.feature.qrscanner.domain.detector.QRContentDetector
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScanSaveResult
import com.epic_engine.swisskit.feature.qrscanner.domain.repository.QRScanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class QRScanRepositoryImpl @Inject constructor(
    private val dao: QRScanDao
) : QRScanRepository {

    override fun observeAll(): Flow<List<QRScan>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun save(content: String, label: String): QRScanSaveResult = runCatching {
        val normalized = QRContentDetector.normalize(content)
        val type = QRContentDetector.detect(content)
        val resolvedLabel = label.ifBlank { QRContentDetector.generateLabel(content, type) }

        val existing = dao.findDuplicate(normalized, type.name)
        val now = System.currentTimeMillis()

        if (existing != null) {
            val mergedLabel = if (label.isNotBlank()) label else existing.label
            val updated = existing.copy(scannedAt = now, label = mergedLabel)
            dao.insert(updated)
            QRScanSaveResult.MergedDuplicate(updated.toDomain())
        } else {
            val entity = QRScanEntity(
                id = UUID.randomUUID().toString(),
                content = content.trim(),
                type = type.name,
                label = resolvedLabel,
                scannedAt = now
            )
            dao.insert(entity)
            QRScanSaveResult.Created(entity.toDomain())
        }
    }.getOrElse { QRScanSaveResult.Failed }

    override suspend fun updateLabel(id: String, label: String) = dao.updateLabel(id, label)

    override suspend fun delete(scan: QRScan) = dao.delete(scan.toEntity())

    override suspend fun deleteAll() = dao.deleteAll()
}
