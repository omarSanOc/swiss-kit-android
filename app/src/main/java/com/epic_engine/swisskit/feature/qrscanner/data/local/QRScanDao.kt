package com.epic_engine.swisskit.feature.qrscanner.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QRScanDao {
    @Query("SELECT * FROM qr_scans ORDER BY scannedAt DESC")
    fun observeAll(): Flow<List<QRScanEntity>>

    @Query("SELECT * FROM qr_scans WHERE content = :normalizedContent AND type = :type LIMIT 1")
    suspend fun findDuplicate(normalizedContent: String, type: String): QRScanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: QRScanEntity)

    @Delete
    suspend fun delete(entity: QRScanEntity)

    @Query("DELETE FROM qr_scans")
    suspend fun deleteAll()

    @Query("UPDATE qr_scans SET label = :label WHERE id = :id")
    suspend fun updateLabel(id: String, label: String)
}
