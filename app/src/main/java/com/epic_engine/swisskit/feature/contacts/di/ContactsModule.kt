package com.epic_engine.swisskit.feature.contacts.di

import com.epic_engine.swisskit.core.database.SwissKitDatabase
import com.epic_engine.swisskit.feature.contacts.data.local.CategoryDao
import com.epic_engine.swisskit.feature.contacts.data.local.ContactDao
import com.epic_engine.swisskit.feature.contacts.data.repository.CategoryRepositoryImpl
import com.epic_engine.swisskit.feature.contacts.data.repository.ContactRepositoryImpl
import com.epic_engine.swisskit.feature.contacts.domain.repository.CategoryRepository
import com.epic_engine.swisskit.feature.contacts.domain.repository.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ContactsModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(impl: ContactRepositoryImpl): ContactRepository

    companion object {
        @Provides
        fun provideCategoryDao(db: SwissKitDatabase): CategoryDao = db.categoryDao()

        @Provides
        fun provideContactDao(db: SwissKitDatabase): ContactDao = db.contactDao()
    }
}
