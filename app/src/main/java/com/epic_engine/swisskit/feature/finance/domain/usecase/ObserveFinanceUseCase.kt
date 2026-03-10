package com.epic_engine.swisskit.feature.finance.domain.usecase

import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceSortOrder
import com.epic_engine.swisskit.feature.finance.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFinanceUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    operator fun invoke(sortOrder: FinanceSortOrder = FinanceSortOrder.DESCENDING): Flow<List<Finance>> =
        repository.observeAll(sortOrder)
}
