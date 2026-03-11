package com.epic_engine.swisskit.feature.qrscanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_scans")
data class QRScanEntity(
    @PrimaryKey val id: String,
    val content: String,
    val type: String,
    val label: String,
    val scannedAt: Long
)
