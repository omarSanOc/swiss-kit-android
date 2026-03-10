package com.epic_engine.swisskit.feature.finance.domain.usecase

import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.repository.FinanceRepository
import javax.inject.Inject

class UpdateFinanceUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(finance: Finance): Result<Unit> {
        if (finance.title.isBlank()) return Result.failure(IllegalArgumentException("El título es requerido"))
        if (finance.amount <= 0) return Result.failure(IllegalArgumentException("El monto debe ser mayor a 0"))
        return repository.update(finance)
    }
}
