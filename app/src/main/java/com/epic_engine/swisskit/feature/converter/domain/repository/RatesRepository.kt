package com.epic_engine.swisskit.feature.converter.domain.repository

import com.epic_engine.swisskit.feature.converter.domain.model.Rates

interface RatesRepository {
    /** Checks cache (30-min TTL); refreshes from API if stale; falls back to stale cache on network error. */
    suspend fun getLatest(forceRefresh: Boolean = false): Result<Rates>
    /** Returns the last cached rates without checking TTL (for offline use). */
    suspend fun getCached(): Rates?
    suspend fun saveSelectedCurrencies(from: String, to: String)
    suspend fun getSelectedCurrencies(): Pair<String, String>?
}
