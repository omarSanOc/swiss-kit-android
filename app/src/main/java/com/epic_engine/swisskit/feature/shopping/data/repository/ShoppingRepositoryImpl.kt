package com.epic_engine.swisskit.feature.shopping.data.repository

import com.epic_engine.swisskit.core.common.SwissKitLogger
import com.epic_engine.swisskit.feature.shopping.data.local.ShoppingDao
import com.epic_engine.swisskit.feature.shopping.data.local.ShoppingItemEntity
import com.epic_engine.swisskit.feature.shopping.data.mapper.toDomain
import com.epic_engine.swisskit.feature.shopping.data.mapper.toEntity
import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem
import com.epic_engine.swisskit.feature.shopping.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ShoppingRepositoryImpl @Inject constructor(
    private val dao: ShoppingDao
) : ShoppingRepository {

    override fun observeItems(): Flow<List<ShoppingItem>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addItem(name: String): Result<ShoppingItem> = runCatching {
        val nextOrder = (dao.getMaxSortOrder() ?: -1) + 1
        val entity = ShoppingItemEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            isChecked = false,
            sortOrder = nextOrder
        )
        dao.insert(entity)
        SwissKitLogger.d("Shopping", "Item agregado: $name")
        entity.toDomain()
    }.onFailure {
        SwissKitLogger.e("Shopping", "Error al agregar item: ${it.message}", it)
    }

    override suspend fun toggleItem(item: ShoppingItem): Result<Unit> = runCatching {
        dao.update(item.toEntity())
        SwissKitLogger.d("Shopping", "Item actualizado: ${item.name}, checked=${item.isChecked}")
    }.onFailure {
        SwissKitLogger.e("Shopping", "Error al actualizar item: ${it.message}", it)
    }

    override suspend fun deleteItem(item: ShoppingItem): Result<Unit> = runCatching {
        dao.delete(item.toEntity())
        SwissKitLogger.d("Shopping", "Item eliminado: ${item.name}")
    }.onFailure {
        SwissKitLogger.e("Shopping", "Error al eliminar item: ${it.message}", it)
    }

    override suspend fun uncheckAll(): Result<Unit> = runCatching {
        dao.uncheckAll()
        SwissKitLogger.d("Shopping", "Todos los items desmarcados")
    }

    override suspend fun deleteChecked(): Result<Unit> = runCatching {
        dao.deleteChecked()
        SwissKitLogger.d("Shopping", "Items marcados eliminados")
    }

    override suspend fun isDuplicate(name: String): Boolean =
        dao.countByName(name.trim()) > 0
}
