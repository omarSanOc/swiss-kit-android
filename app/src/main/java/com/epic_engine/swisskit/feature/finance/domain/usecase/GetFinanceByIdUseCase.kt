package com.epic_engine.swisskit.feature.finance.domain.usecase

import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.repository.FinanceRepository
import javax.inject.Inject

class GetFinanceByIdUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(id: String): Finance? = repository.getById(id)
}
