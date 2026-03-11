package com.epic_engine.swisskit.feature.qrscanner.domain.repository

import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScanSaveResult
import kotlinx.coroutines.flow.Flow

interface QRScanRepository {
    fun observeAll(): Flow<List<QRScan>>
    suspend fun save(content: String): QRScanSaveResult
    suspend fun delete(scan: QRScan)
    suspend fun deleteAll()
}
