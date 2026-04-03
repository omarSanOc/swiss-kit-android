package com.epic_engine.swisskit.feature.converter.data.repository

import com.epic_engine.swisskit.feature.converter.data.local.RatesLocalDataSource
import com.epic_engine.swisskit.feature.converter.data.remote.ExchangeRateApiService
import com.epic_engine.swisskit.feature.converter.data.remote.dto.CurrencyResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class RatesRepositoryImplTest {

    private lateinit var apiService: ExchangeRateApiService
    private lateinit var localDataSource: RatesLocalDataSource
    private lateinit var repository: RatesRepositoryImpl

    private val freshDto = CurrencyResponseDto(base = "USD", rates = mapOf("EUR" to 0.91, "MXN" to 17.5))
    private val staleDto = CurrencyResponseDto(base = "USD", rates = mapOf("EUR" to 0.85, "MXN" to 17.0))

    @Before
    fun setUp() {
        apiService = mockk()
        localDataSource = mockk()
        repository = RatesRepositoryImpl(apiService, localDataSource)
    }

    @Test
    fun `forceRefresh false with fresh cache returns cached rates without calling API`() = runTest {
        coEvery { localDataSource.getCachedRates() } returns freshDto

        val result = repository.getLatest(forceRefresh = false)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isFromCache)
        coVerify(exactly = 0) { apiService.getLatestRates(any()) }
    }

    @Test
    fun `forceRefresh true ignores fresh cache and calls API`() = runTest {
        coEvery { localDataSource.saveRates(any()) } returns Unit
        coEvery { apiService.getLatestRates(any()) } returns freshDto

        val result = repository.getLatest(forceRefresh = true)

        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!.isFromCache)
        coVerify(exactly = 1) { apiService.getLatestRates(any()) }
        // getCachedRates should never be consulted during a forceRefresh
        coVerify(exactly = 0) { localDataSource.getCachedRates() }
    }

    @Test
    fun `forceRefresh true with network error falls back to stale cache`() = runTest {
        coEvery { apiService.getLatestRates(any()) } throws IOException("Sin red")
        coEvery { localDataSource.getStaleRates() } returns staleDto

        val result = repository.getLatest(forceRefresh = true)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isFromCache)
    }

    @Test
    fun `forceRefresh true with network error and no cache returns failure`() = runTest {
        coEvery { apiService.getLatestRates(any()) } throws IOException("Sin red")
        coEvery { localDataSource.getStaleRates() } returns null

        val result = repository.getLatest(forceRefresh = true)

        assertTrue(result.isFailure)
    }

    @Test
    fun `forceRefresh false with expired cache calls API`() = runTest {
        coEvery { localDataSource.getCachedRates() } returns null  // TTL expired
        coEvery { apiService.getLatestRates(any()) } returns freshDto
        coEvery { localDataSource.saveRates(any()) } returns Unit

        val result = repository.getLatest(forceRefresh = false)

        assertTrue(result.isSuccess)
        assertFalse(result.getOrNull()!!.isFromCache)
        coVerify(exactly = 1) { apiService.getLatestRates(any()) }
    }
}
