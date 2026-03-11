package com.epic_engine.swisskit.feature.qrscanner.domain.usecase

import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScanSaveResult
import com.epic_engine.swisskit.feature.qrscanner.domain.repository.QRScanRepository
import javax.inject.Inject

class SaveQRScanUseCase @Inject constructor(private val repo: QRScanRepository) {
    suspend operator fun invoke(content: String): QRScanSaveResult = repo.save(content)
}
