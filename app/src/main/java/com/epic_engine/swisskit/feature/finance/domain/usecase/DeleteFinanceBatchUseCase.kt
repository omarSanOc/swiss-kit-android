package com.epic_engine.swisskit.feature.finance.domain.usecase

import com.epic_engine.swisskit.feature.finance.domain.repository.FinanceRepository
import javax.inject.Inject

class DeleteFinanceBatchUseCase @Inject constructor(private val repository: FinanceRepository) {
    suspend operator fun invoke(ids: Set<String>): Result<Unit> = repository.deleteByIds(ids)
}
