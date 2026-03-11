package com.epic_engine.swisskit.feature.contacts.data.mapper

import com.epic_engine.swisskit.feature.contacts.data.local.CategoryEntity
import com.epic_engine.swisskit.feature.contacts.data.local.CategoryWithContacts
import com.epic_engine.swisskit.feature.contacts.domain.model.Category
import com.epic_engine.swisskit.feature.contacts.domain.model.Contact

fun CategoryEntity.toDomain(contacts: List<Contact> = emptyList()) = Category(
    id = id,
    title = title,
    contacts = contacts
)

fun CategoryWithContacts.toDomain() = Category(
    id = category.id,
    title = category.title,
    contacts = contacts.map { it.toDomain() }
)

fun Category.toEntity() = CategoryEntity(id = id, title = title)
