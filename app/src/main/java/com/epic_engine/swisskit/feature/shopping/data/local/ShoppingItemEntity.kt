package com.epic_engine.swisskit.feature.shopping.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isChecked: Boolean,
    val sortOrder: Int
)
