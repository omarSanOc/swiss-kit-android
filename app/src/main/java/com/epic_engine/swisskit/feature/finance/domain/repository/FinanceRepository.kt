package com.epic_engine.swisskit.feature.finance.domain.repository

import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceSortOrder
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun observeAll(sortOrder: FinanceSortOrder = FinanceSortOrder.DESCENDING): Flow<List<Finance>>
    suspend fun getById(id: String): Finance?
    suspend fun add(finance: Finance): Result<Unit>
    suspend fun update(finance: Finance): Result<Unit>
    suspend fun delete(finance: Finance): Result<Unit>
    suspend fun deleteByIds(ids: Set<String>): Result<Unit>
    suspend fun getAll(): Result<List<Finance>>
    suspend fun deleteAll(): Result<Unit>
    suspend fun insertAll(items: List<Finance>): Result<Unit>
}
