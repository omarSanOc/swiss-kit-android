package com.epic_engine.swisskit.feature.shopping.di

import com.epic_engine.swisskit.core.database.SwissKitDatabase
import com.epic_engine.swisskit.feature.shopping.data.local.ShoppingDao
import com.epic_engine.swisskit.feature.shopping.data.repository.ShoppingRepositoryImpl
import com.epic_engine.swisskit.feature.shopping.domain.repository.ShoppingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
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

@Module
@InstallIn(SingletonComponent::class)
object ShoppingDaoModule {

    @Provides
    fun provideShoppingDao(database: SwissKitDatabase): ShoppingDao =
        database.shoppingDao()
}
