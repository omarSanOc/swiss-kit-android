package com.epic_engine.swisskit.feature.qrscanner.domain.model

data class QRScan(
    val id: String,
    val content: String,
    val type: QRContentType,
    val label: String,
    val scannedAt: Long
)
