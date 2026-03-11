package com.epic_engine.swisskit.feature.contacts.domain.repository

import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun observeByCategory(categoryId: String): Flow<List<Contact>>
    fun searchInCategory(categoryId: String, query: String): Flow<List<Contact>>
    suspend fun add(name: String, phone: String, categoryId: String): Contact
    suspend fun update(contact: Contact)
    suspend fun delete(contact: Contact)
    suspend fun deleteAll(categoryId: String)
}
