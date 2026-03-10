package com.epic_engine.swisskit.feature.finance.di

import com.epic_engine.swisskit.feature.finance.data.repository.FinanceRepositoryImpl
import com.epic_engine.swisskit.feature.finance.domain.repository.FinanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FinanceModule {

    @Binds
    @Singleton
    abstract fun bindFinanceRepository(
        impl: FinanceRepositoryImpl
    ): FinanceRepository
}
