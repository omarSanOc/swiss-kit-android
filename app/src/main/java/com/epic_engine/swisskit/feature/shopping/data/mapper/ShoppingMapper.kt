package com.epic_engine.swisskit.feature.shopping.data.mapper

import com.epic_engine.swisskit.feature.shopping.data.local.ShoppingItemEntity
import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem

fun ShoppingItemEntity.toDomain(): ShoppingItem = ShoppingItem(
    id = id,
    name = name,
    isChecked = isChecked,
    sortOrder = sortOrder
)

fun ShoppingItem.toEntity(): ShoppingItemEntity = ShoppingItemEntity(
    id = id,
    name = name,
    isChecked = isChecked,
    sortOrder = sortOrder
)
