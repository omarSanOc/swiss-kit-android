package com.epic_engine.swisskit.feature.converter.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.epic_engine.swisskit.core.common.SwissKitLogger
import com.epic_engine.swisskit.feature.converter.data.remote.dto.CurrencyResponseDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.ratesDataStore: DataStore<Preferences> by preferencesDataStore(name = "converter_rates_cache")

@Singleton
class RatesLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_RATES_JSON = stringPreferencesKey("rates_json")
        private val KEY_TIMESTAMP = longPreferencesKey("rates_timestamp")
        private val KEY_CURRENCY_FROM = stringPreferencesKey("selected_currency_from")
        private val KEY_CURRENCY_TO = stringPreferencesKey("selected_currency_to")

        /** TTL de 30 minutos en milisegundos — equivalente al iOS RatesCache */
        const val CACHE_TTL_MS = 30 * 60 * 1_000L
    }

    /** Guarda la respuesta de la API con timestamp actual */
    suspend fun saveRates(dto: CurrencyResponseDto) {
        context.ratesDataStore.edit { prefs ->
            prefs[KEY_RATES_JSON] = Json.encodeToString(dto)
            prefs[KEY_TIMESTAMP] = System.currentTimeMillis()
        }
        SwissKitLogger.d("Converter", "Rates guardados en cache")
    }

    /** Retorna los rates cacheados si existen y no han expirado (TTL 30 min) */
    suspend fun getCachedRates(): CurrencyResponseDto? {
        val prefs = context.ratesDataStore.data.firstOrNull() ?: return null
        val json = prefs[KEY_RATES_JSON] ?: return null
        val timestamp = prefs[KEY_TIMESTAMP] ?: return null

        val age = System.currentTimeMillis() - timestamp
        if (age > CACHE_TTL_MS) {
            SwissKitLogger.d("Converter", "Cache expirado (${age / 1000}s > 1800s)")
            return null
        }

        return try {
            Json.decodeFromString<CurrencyResponseDto>(json)
        } catch (e: Exception) {
            SwissKitLogger.e("Converter", "Error al decodificar cache", e)
            null
        }
    }

    /** Retorna los rates cacheados SIN verificar TTL (para fallback offline) */
    suspend fun getStaleRates(): CurrencyResponseDto? {
        val prefs = context.ratesDataStore.data.firstOrNull() ?: return null
        val json = prefs[KEY_RATES_JSON] ?: return null
        return try {
            Json.decodeFromString<CurrencyResponseDto>(json)
        } catch (e: Exception) {
            null
        }
    }

    /** Persists the user's selected currencies. */
    suspend fun saveSelectedCurrencies(from: String, to: String) {
        context.ratesDataStore.edit { prefs ->
            prefs[KEY_CURRENCY_FROM] = from
            prefs[KEY_CURRENCY_TO] = to
        }
    }

    /** Reads the previously saved currency selection. */
    suspend fun getSelectedCurrencies(): Pair<String, String>? {
        val prefs = context.ratesDataStore.data.firstOrNull() ?: return null
        val from = prefs[KEY_CURRENCY_FROM] ?: return null
        val to = prefs[KEY_CURRENCY_TO] ?: return null
        return Pair(from, to)
    }
}
