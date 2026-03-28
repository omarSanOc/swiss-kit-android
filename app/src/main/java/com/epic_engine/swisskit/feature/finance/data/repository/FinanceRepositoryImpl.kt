package com.epic_engine.swisskit.feature.finance.data.repository

import com.epic_engine.swisskit.core.common.SwissKitLogger
import com.epic_engine.swisskit.feature.finance.data.local.FinanceDao
import com.epic_engine.swisskit.feature.finance.data.mapper.toDomain
import com.epic_engine.swisskit.feature.finance.data.mapper.toEntity
import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceSortOrder
import com.epic_engine.swisskit.feature.finance.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FinanceRepositoryImpl @Inject constructor(
    private val dao: FinanceDao
) : FinanceRepository {

    override fun observeAll(sortOrder: FinanceSortOrder): Flow<List<Finance>> =
        if (sortOrder == FinanceSortOrder.DESCENDING) dao.observeAll().map { it.map { e -> e.toDomain() } }
        else dao.observeAllAscending().map { it.map { e -> e.toDomain() } }

    override suspend fun getById(id: String): Finance? =
        dao.getById(id)?.toDomain()

    override suspend fun add(finance: Finance): Result<Unit> = runCatching {
        dao.insert(finance.toEntity())
        SwissKitLogger.d("Finance", "Transacción agregada: ${finance.title}")
    }

    override suspend fun update(finance: Finance): Result<Unit> = runCatching {
        dao.update(finance.toEntity())
        SwissKitLogger.d("Finance", "Transacción actualizada: ${finance.title}")
    }

    override suspend fun delete(finance: Finance): Result<Unit> = runCatching {
        dao.delete(finance.toEntity())
    }

    override suspend fun deleteByIds(ids: Set<String>): Result<Unit> = runCatching {
        dao.deleteByIds(ids.toList())
        SwissKitLogger.d("Finance", "Eliminadas ${ids.size} transacciones en lote")
    }

    override suspend fun getAll(): Result<List<Finance>> = runCatching {
        dao.getAll().map { it.toDomain() }
    }

    override suspend fun deleteAll(): Result<Unit> = runCatching { dao.deleteAll() }

    override suspend fun insertAll(items: List<Finance>): Result<Unit> = runCatching {
        items.forEach { dao.insert(it.toEntity()) }
        SwissKitLogger.d("Finance", "Restauradas ${items.size} transacciones desde backup")
    }

    override fun observeDistinctCategories(): Flow<List<String>> =
        dao.observeDistinctCategories()
}
