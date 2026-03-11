package com.epic_engine.swisskit.feature.contacts.data.repository

import com.epic_engine.swisskit.feature.contacts.data.local.ContactDao
import com.epic_engine.swisskit.feature.contacts.data.local.ContactEntity
import com.epic_engine.swisskit.feature.contacts.data.mapper.toDomain
import com.epic_engine.swisskit.feature.contacts.data.mapper.toEntity
import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val dao: ContactDao
) : ContactRepository {

    override fun observeByCategory(categoryId: String): Flow<List<Contact>> =
        dao.observeByCategory(categoryId).map { list -> list.map { it.toDomain() } }

    override fun searchInCategory(categoryId: String, query: String): Flow<List<Contact>> =
        dao.searchInCategory(categoryId, query).map { list -> list.map { it.toDomain() } }

    override suspend fun add(name: String, phone: String, categoryId: String): Contact {
        val entity = ContactEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            phone = phone,
            categoryId = categoryId
        )
        dao.insert(entity)
        return entity.toDomain()
    }

    override suspend fun update(contact: Contact) = dao.update(contact.toEntity())

    override suspend fun delete(contact: Contact) = dao.delete(contact.toEntity())

    override suspend fun deleteAll(categoryId: String) = dao.deleteAllInCategory(categoryId)
}
