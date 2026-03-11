package com.epic_engine.swisskit.feature.qrscanner.domain.usecase

import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.repository.QRScanRepository
import javax.inject.Inject

class DeleteQRScanUseCase @Inject constructor(private val repo: QRScanRepository) {
    suspend operator fun invoke(scan: QRScan) = repo.delete(scan)
}
