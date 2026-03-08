package com.epic_engine.swisskit.feature.converter.di

import com.epic_engine.swisskit.feature.converter.data.repository.RatesRepositoryImpl
import com.epic_engine.swisskit.feature.converter.domain.repository.RatesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConverterModule {

    @Binds
    @Singleton
    abstract fun bindRatesRepository(
        impl: RatesRepositoryImpl
    ): RatesRepository
}
