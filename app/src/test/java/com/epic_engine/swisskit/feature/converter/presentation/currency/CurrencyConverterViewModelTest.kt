package com.epic_engine.swisskit.feature.converter.presentation.currency

import com.epic_engine.swisskit.feature.converter.domain.model.Rates
import com.epic_engine.swisskit.feature.converter.domain.usecase.GetLatestRatesUseCase
import com.epic_engine.swisskit.feature.converter.domain.usecase.GetSelectedCurrenciesUseCase
import com.epic_engine.swisskit.feature.converter.domain.usecase.SaveSelectedCurrenciesUseCase
import com.epic_engine.swisskit.feature.converter.presentation.utils.CurrencyConverterEvent
import com.epic_engine.swisskit.feature.converter.presentation.viewmodel.CurrencyConverterViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.CompletableDeferred

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyConverterViewModelTest {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private lateinit var getLatestRates: GetLatestRatesUseCase
    private lateinit var getSelectedCurrencies: GetSelectedCurrenciesUseCase
    private lateinit var saveSelectedCurrencies: SaveSelectedCurrenciesUseCase

    private val fakeRates = Rates(
        base = "USD",
        values = mapOf("EUR" to 0.91, "MXN" to 17.5),
        isFromCache = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getLatestRates = mockk()
        getSelectedCurrencies = mockk()
        saveSelectedCurrencies = mockk()
        // loadSavedPreferences() always called in init
        coEvery { getSelectedCurrencies() } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load sets isLoading true before rates arrive`() = runTest {
        val deferred = CompletableDeferred<Result<Rates>>()
        coEvery { getLatestRates(false) } coAnswers { deferred.await() }

        val viewModel = CurrencyConverterViewModel(getLatestRates, getSelectedCurrencies, saveSelectedCurrencies)

        // With UnconfinedTestDispatcher, the coroutine runs until getLatestRates suspends
        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isRefreshing)

        deferred.complete(Result.success(fakeRates))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `Refresh event sets isRefreshing true and not isLoading when rates already loaded`() = runTest {
        // First: let initial load complete
        coEvery { getLatestRates(false) } returns Result.success(fakeRates)
        val viewModel = CurrencyConverterViewModel(getLatestRates, getSelectedCurrencies, saveSelectedCurrencies)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.rates)

        // Second: suspend the refresh call
        val deferred = CompletableDeferred<Result<Rates>>()
        coEvery { getLatestRates(true) } coAnswers { deferred.await() }

        viewModel.onEvent(CurrencyConverterEvent.Refresh)

        assertTrue(viewModel.uiState.value.isRefreshing)
        assertFalse(viewModel.uiState.value.isLoading)

        deferred.complete(Result.success(fakeRates))
        advanceUntilIdle()
    }

    @Test
    fun `isRefreshing returns to false after successful refresh`() = runTest {
        coEvery { getLatestRates(false) } returns Result.success(fakeRates)
        val viewModel = CurrencyConverterViewModel(getLatestRates, getSelectedCurrencies, saveSelectedCurrencies)
        advanceUntilIdle()

        val updatedRates = fakeRates.copy(values = mapOf("EUR" to 0.92))
        coEvery { getLatestRates(true) } returns Result.success(updatedRates)

        viewModel.onEvent(CurrencyConverterEvent.Refresh)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRefreshing)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `isRefreshing returns to false after failed refresh`() = runTest {
        coEvery { getLatestRates(false) } returns Result.success(fakeRates)
        val viewModel = CurrencyConverterViewModel(getLatestRates, getSelectedCurrencies, saveSelectedCurrencies)
        advanceUntilIdle()

        coEvery { getLatestRates(true) } returns Result.failure(Exception("Sin conexión"))

        viewModel.onEvent(CurrencyConverterEvent.Refresh)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRefreshing)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `failed refresh preserves previously loaded rates`() = runTest {
        coEvery { getLatestRates(false) } returns Result.success(fakeRates)
        val viewModel = CurrencyConverterViewModel(getLatestRates, getSelectedCurrencies, saveSelectedCurrencies)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.rates)

        coEvery { getLatestRates(true) } returns Result.failure(Exception("Sin conexión"))

        viewModel.onEvent(CurrencyConverterEvent.Refresh)
        advanceUntilIdle()

        // Rates should still be visible from before the failed refresh
        assertNotNull(viewModel.uiState.value.rates)
    }

    @Test
    fun `initial load failure leaves isLoading false with error message`() = runTest {
        coEvery { getLatestRates(false) } returns Result.failure(Exception("Sin conexión"))

        val viewModel = CurrencyConverterViewModel(getLatestRates, getSelectedCurrencies, saveSelectedCurrencies)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.rates)
    }
}
