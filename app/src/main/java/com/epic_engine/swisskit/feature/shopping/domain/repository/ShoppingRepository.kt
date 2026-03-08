package com.epic_engine.swisskit.feature.shopping.domain.repository

import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {
    fun observeItems(): Flow<List<ShoppingItem>>
    suspend fun addItem(name: String): Result<ShoppingItem>
    suspend fun toggleItem(item: ShoppingItem): Result<Unit>
    suspend fun deleteItem(item: ShoppingItem): Result<Unit>
    suspend fun uncheckAll(): Result<Unit>
    suspend fun deleteChecked(): Result<Unit>
    suspend fun isDuplicate(name: String): Boolean
}
