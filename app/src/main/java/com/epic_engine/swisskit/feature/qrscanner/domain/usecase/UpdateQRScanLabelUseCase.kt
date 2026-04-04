package com.epic_engine.swisskit.feature.qrscanner.domain.usecase

import com.epic_engine.swisskit.feature.qrscanner.domain.repository.QRScanRepository
import javax.inject.Inject

class UpdateQRScanLabelUseCase @Inject constructor(private val repo: QRScanRepository) {
    suspend operator fun invoke(id: String, label: String) = repo.updateLabel(id, label)
}
