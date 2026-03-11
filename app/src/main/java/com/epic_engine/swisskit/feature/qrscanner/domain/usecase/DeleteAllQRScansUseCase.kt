package com.epic_engine.swisskit.feature.qrscanner.domain.usecase

import com.epic_engine.swisskit.feature.qrscanner.domain.repository.QRScanRepository
import javax.inject.Inject

class DeleteAllQRScansUseCase @Inject constructor(private val repo: QRScanRepository) {
    suspend operator fun invoke() = repo.deleteAll()
}
