package com.epic_engine.swisskit.feature.qrscanner.domain.repository

import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScanSaveResult
import kotlinx.coroutines.flow.Flow

interface QRScanRepository {
    fun observeAll(): Flow<List<QRScan>>
    suspend fun save(content: String, label: String = ""): QRScanSaveResult
    suspend fun updateLabel(id: String, label: String)
    suspend fun delete(scan: QRScan)
    suspend fun deleteAll()
}
