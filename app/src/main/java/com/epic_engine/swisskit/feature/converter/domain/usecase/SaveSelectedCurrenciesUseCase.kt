package com.epic_engine.swisskit.feature.converter.domain.usecase

import com.epic_engine.swisskit.feature.converter.domain.repository.RatesRepository
import javax.inject.Inject

class SaveSelectedCurrenciesUseCase @Inject constructor(
    private val repository: RatesRepository
) {
    suspend operator fun invoke(from: String, to: String) = repository.saveSelectedCurrencies(from, to)
}
