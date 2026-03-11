package com.epic_engine.swisskit.feature.qrscanner.domain.model

sealed class QRScanSaveResult {
    data class Created(val scan: QRScan) : QRScanSaveResult()
    data class MergedDuplicate(val scan: QRScan) : QRScanSaveResult()
    data object Failed : QRScanSaveResult()
}
