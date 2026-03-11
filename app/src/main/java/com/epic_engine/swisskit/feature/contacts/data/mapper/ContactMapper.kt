package com.epic_engine.swisskit.feature.contacts.data.mapper

import com.epic_engine.swisskit.feature.contacts.data.local.ContactEntity
import com.epic_engine.swisskit.feature.contacts.domain.model.Contact

fun ContactEntity.toDomain() = Contact(
    id = id,
    name = name,
    phone = phone,
    categoryId = categoryId
)

fun Contact.toEntity() = ContactEntity(
    id = id,
    name = name,
    phone = phone,
    categoryId = categoryId
)
