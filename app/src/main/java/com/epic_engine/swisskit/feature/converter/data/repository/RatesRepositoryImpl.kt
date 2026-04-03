package com.epic_engine.swisskit.feature.converter.data.repository

import com.epic_engine.swisskit.core.common.SwissKitLogger
import com.epic_engine.swisskit.feature.converter.data.local.RatesLocalDataSource
import com.epic_engine.swisskit.feature.converter.data.mapper.toDomain
import com.epic_engine.swisskit.feature.converter.data.remote.ExchangeRateApiService
import com.epic_engine.swisskit.feature.converter.domain.model.Rates
import com.epic_engine.swisskit.feature.converter.domain.repository.RatesRepository
import javax.inject.Inject

class RatesRepositoryImpl @Inject constructor(
    private val apiService: ExchangeRateApiService,
    private val localDataSource: RatesLocalDataSource
) : RatesRepository {

    /**
     * Estrategia offline-first:
     * 1. Verifica cache con TTL 30 min → retorna si fresco
     * 2. Llama a la API (con retry en el interceptor OkHttp)
     * 3. Si la API falla → fallback a cache stale (sin importar TTL)
     * 4. Si no hay cache → retorna error
     */
    override suspend fun getLatest(forceRefresh: Boolean): Result<Rates> {
        // 1. Cache fresco (omitido si forceRefresh = true)
        if (!forceRefresh) {
            val cached = localDataSource.getCachedRates()
            if (cached != null) {
                SwissKitLogger.d("Converter", "Sirviendo rates desde cache fresco")
                return Result.success(cached.toDomain(isFromCache = true))
            }
        }

        // 2. Llamada a la API
        return try {
            val dto = apiService.getLatestRates()
            localDataSource.saveRates(dto)
            SwissKitLogger.d("Converter", "Rates actualizados desde API (base=${dto.base})")
            Result.success(dto.toDomain(isFromCache = false))
        } catch (e: Exception) {
            SwissKitLogger.w("Converter", "Error de red: ${e.message}. Intentando cache stale...")

            // 3. Fallback a cache stale
            val stale = localDataSource.getStaleRates()
            if (stale != null) {
                SwissKitLogger.i("Converter", "Sirviendo rates desde cache stale (modo offline)")
                Result.success(stale.toDomain(isFromCache = true))
            } else {
                SwissKitLogger.e("Converter", "Sin cache disponible y sin red", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getCached(): Rates? =
        localDataSource.getStaleRates()?.toDomain(isFromCache = true)

    override suspend fun saveSelectedCurrencies(from: String, to: String) =
        localDataSource.saveSelectedCurrencies(from, to)

    override suspend fun getSelectedCurrencies(): Pair<String, String>? =
        localDataSource.getSelectedCurrencies()
}
