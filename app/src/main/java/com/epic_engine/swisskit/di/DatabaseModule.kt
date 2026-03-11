package com.epic_engine.swisskit.di

import android.content.Context
import androidx.room.Room
import com.epic_engine.swisskit.core.database.MIGRATION_1_2
import com.epic_engine.swisskit.core.database.MIGRATION_2_3
import com.epic_engine.swisskit.core.database.MIGRATION_3_4
import com.epic_engine.swisskit.core.database.MIGRATION_4_5
import com.epic_engine.swisskit.core.database.SwissKitDatabase
import com.epic_engine.swisskit.feature.finance.data.local.FinanceDao
import com.epic_engine.swisskit.feature.shopping.data.local.ShoppingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SwissKitDatabase =
        Room.databaseBuilder(context, SwissKitDatabase::class.java, "swisskit.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .build()

    @Provides
    fun provideShoppingDao(db: SwissKitDatabase): ShoppingDao = db.shoppingDao()

    @Provides
    fun provideFinanceDao(db: SwissKitDatabase): FinanceDao = db.financeDao()
}
