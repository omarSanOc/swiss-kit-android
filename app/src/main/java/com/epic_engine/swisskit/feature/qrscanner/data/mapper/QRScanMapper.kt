package com.epic_engine.swisskit.feature.qrscanner.data.mapper

import com.epic_engine.swisskit.feature.qrscanner.data.local.QRScanEntity
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan

fun QRScanEntity.toDomain() = QRScan(
    id = id,
    content = content,
    type = runCatching { QRContentType.valueOf(type) }.getOrDefault(QRContentType.TEXT),
    label = label,
    scannedAt = scannedAt
)

fun QRScan.toEntity() = QRScanEntity(
    id = id,
    content = content,
    type = type.name,
    label = label,
    scannedAt = scannedAt
)
