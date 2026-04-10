package com.epic_engine.swisskit.feature.converter.domain.usecase

import com.epic_engine.swisskit.feature.converter.domain.repository.RatesRepository
import javax.inject.Inject

class GetSelectedCurrenciesUseCase @Inject constructor(
    private val repository: RatesRepository
) {
    suspend operator fun invoke(): Pair<String, String>? = repository.getSelectedCurrencies()
}
