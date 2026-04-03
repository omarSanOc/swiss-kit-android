package com.epic_engine.swisskit.feature.converter.domain.repository

import com.epic_engine.swisskit.feature.converter.domain.model.Rates

interface RatesRepository {
    /** Verifica cache (TTL 30 min) → si expirado, llama API → fallback a cache stale si hay error de red */
    suspend fun getLatest(forceRefresh: Boolean = false): Result<Rates>
    /** Devuelve los últimos rates guardados sin verificar TTL (para uso offline) */
    suspend fun getCached(): Rates?
    suspend fun saveSelectedCurrencies(from: String, to: String)
    suspend fun getSelectedCurrencies(): Pair<String, String>?
}
