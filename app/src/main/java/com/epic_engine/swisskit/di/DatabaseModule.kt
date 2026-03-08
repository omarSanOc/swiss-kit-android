package com.epic_engine.swisskit.di

import android.content.Context
import androidx.room.Room
import com.epic_engine.swisskit.core.database.SwissKitDatabase
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SwissKitDatabase = Room.databaseBuilder(
        context,
        SwissKitDatabase::class.java,
        "swisskit.db"
    ).build()
}
