package com.epic_engine.swisskit.feature.contacts.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithContacts(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val contacts: List<ContactEntity>
)
