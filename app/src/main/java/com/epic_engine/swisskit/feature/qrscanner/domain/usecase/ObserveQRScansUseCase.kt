package com.epic_engine.swisskit.feature.qrscanner.domain.usecase

import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.domain.repository.QRScanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveQRScansUseCase @Inject constructor(private val repo: QRScanRepository) {
    operator fun invoke(): Flow<List<QRScan>> = repo.observeAll()
}
