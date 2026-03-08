package com.epic_engine.swisskit.feature.shopping.domain.model

data class ShoppingItem(
    val id: String,
    val name: String,
    val isChecked: Boolean,
    val sortOrder: Int
)
