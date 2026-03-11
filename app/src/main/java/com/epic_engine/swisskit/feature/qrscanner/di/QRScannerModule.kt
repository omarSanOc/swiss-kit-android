package com.epic_engine.swisskit.feature.qrscanner.di

import com.epic_engine.swisskit.core.database.SwissKitDatabase
import com.epic_engine.swisskit.feature.qrscanner.data.local.QRScanDao
import com.epic_engine.swisskit.feature.qrscanner.data.repository.QRScanRepositoryImpl
import com.epic_engine.swisskit.feature.qrscanner.domain.repository.QRScanRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class QRScannerModule {

    @Binds
    @Singleton
    abstract fun bindQRScanRepository(impl: QRScanRepositoryImpl): QRScanRepository

    companion object {
        @Provides
        fun provideQRScanDao(db: SwissKitDatabase): QRScanDao = db.qrScanDao()
    }
}
