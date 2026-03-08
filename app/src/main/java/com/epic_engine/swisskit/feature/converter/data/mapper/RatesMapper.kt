package com.epic_engine.swisskit.feature.converter.data.mapper

import com.epic_engine.swisskit.feature.converter.data.remote.dto.CurrencyResponseDto
import com.epic_engine.swisskit.feature.converter.domain.model.CurrencyCatalog
import com.epic_engine.swisskit.feature.converter.domain.model.Rates

/**
 * Convierte DTO de API a modelo de dominio.
 * Filtra las divisas al CurrencyCatalog (15 soportadas), igual que el iOS RatesRepositoryImpl.
 */
fun CurrencyResponseDto.toDomain(isFromCache: Boolean = false): Rates = Rates(
    base = base,
    values = rates.filterKeys { it in CurrencyCatalog.supportedCodes },
    isFromCache = isFromCache
)
