package com.epic_engine.swisskit.feature.contacts.data.repository

import com.epic_engine.swisskit.feature.contacts.data.local.CategoryDao
import com.epic_engine.swisskit.feature.contacts.data.local.CategoryEntity
import com.epic_engine.swisskit.feature.contacts.data.mapper.toDomain
import com.epic_engine.swisskit.feature.contacts.data.mapper.toEntity
import com.epic_engine.swisskit.feature.contacts.domain.model.Category
import com.epic_engine.swisskit.feature.contacts.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun observeAll(): Flow<List<Category>> =
        dao.observeAllWithContacts().map { list -> list.map { it.toDomain() } }

    override suspend fun getCategoryWithContacts(categoryId: String): Category? =
        dao.getCategoryWithContacts(categoryId)?.toDomain()

    override suspend fun add(title: String): Category {
        val entity = CategoryEntity(
            id = UUID.randomUUID().toString(),
            title = title
        )
        dao.insert(entity)
        return entity.toDomain()
    }

    override suspend fun rename(categoryId: String, newTitle: String) {
        val entity = CategoryEntity(id = categoryId, title = newTitle)
        dao.update(entity)
    }

    override suspend fun delete(categoryId: String) {
        dao.delete(CategoryEntity(id = categoryId, title = ""))
    }
}
