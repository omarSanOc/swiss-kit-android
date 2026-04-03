package com.epic_engine.swisskit.feature.converter.domain.usecase

import com.epic_engine.swisskit.feature.converter.domain.model.Rates
import com.epic_engine.swisskit.feature.converter.domain.repository.RatesRepository
import javax.inject.Inject

class GetLatestRatesUseCase @Inject constructor(
    private val repository: RatesRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<Rates> =
        repository.getLatest(forceRefresh)
}
