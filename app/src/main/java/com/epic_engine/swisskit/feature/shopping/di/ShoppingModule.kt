package com.epic_engine.swisskit.feature.shopping.di

import com.epic_engine.swisskit.feature.shopping.data.repository.ShoppingRepositoryImpl
import com.epic_engine.swisskit.feature.shopping.domain.repository.ShoppingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ShoppingModule {

    @Binds
    @Singleton
    abstract fun bindShoppingRepository(
        impl: ShoppingRepositoryImpl
    ): ShoppingRepository
}
