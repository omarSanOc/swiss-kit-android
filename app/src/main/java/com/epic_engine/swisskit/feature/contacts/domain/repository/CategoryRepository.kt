package com.epic_engine.swisskit.feature.contacts.domain.repository

import com.epic_engine.swisskit.feature.contacts.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeAll(): Flow<List<Category>>
    suspend fun getCategoryWithContacts(categoryId: String): Category?
    suspend fun add(title: String): Category
    suspend fun rename(categoryId: String, newTitle: String)
    suspend fun delete(categoryId: String)
}
