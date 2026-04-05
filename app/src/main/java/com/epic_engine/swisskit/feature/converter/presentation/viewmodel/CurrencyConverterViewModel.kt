package com.epic_engine.swisskit.feature.converter.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic_engine.swisskit.core.common.SwissKitLogger
import com.epic_engine.swisskit.feature.converter.domain.model.CurrencyCatalog
import com.epic_engine.swisskit.feature.converter.domain.model.Currency
import com.epic_engine.swisskit.feature.converter.domain.usecase.GetLatestRatesUseCase
import com.epic_engine.swisskit.feature.converter.domain.repository.RatesRepository
import com.epic_engine.swisskit.feature.converter.presentation.utils.CurrencyConverterEvent
import com.epic_engine.swisskit.feature.converter.presentation.utils.CurrencyConverterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val getLatestRates: GetLatestRatesUseCase,
    private val ratesRepository: RatesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyConverterUiState())
    val uiState: StateFlow<CurrencyConverterUiState> = _uiState.asStateFlow()

    // Flow interno del input para debounce 400ms (equivalente iOS)
    private val _amountFlow = MutableStateFlow("")

    init {
        loadSavedPreferences()
        loadRates()
        observeAmountWithDebounce()
    }

    fun onEvent(event: CurrencyConverterEvent) {
        when (event) {
            is CurrencyConverterEvent.AmountChanged -> {
                _uiState.update { it.copy(amountInput = event.input) }
                _amountFlow.value = event.input
            }
            is CurrencyConverterEvent.FromCurrencySelected -> {
                _uiState.update { it.copy(fromCurrency = event.currency) }
                persistCurrencySelection()
                recalculate()
            }
            is CurrencyConverterEvent.ToCurrencySelected -> {
                _uiState.update { it.copy(toCurrency = event.currency) }
                persistCurrencySelection()
                recalculate()
            }
            is CurrencyConverterEvent.SwapCurrencies -> {
                _uiState.update { state ->
                    state.copy(
                        fromCurrency = state.toCurrency,
                        toCurrency = state.fromCurrency
                    )
                }
                persistCurrencySelection()
                recalculate()
            }
            is CurrencyConverterEvent.Refresh -> loadRates(forceRefresh = true)
            is CurrencyConverterEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            is CurrencyConverterEvent.ShowCopiedToast -> _uiState.update { it.copy(showCopiedToast = true) }
            is CurrencyConverterEvent.DismissCopiedToast -> _uiState.update { it.copy(showCopiedToast = false) }
        }
    }

    private fun loadSavedPreferences() {
        viewModelScope.launch {
            val saved = ratesRepository.getSelectedCurrencies()
            if (saved != null) {
                val from = CurrencyCatalog.findByCode(saved.first)
                val to = CurrencyCatalog.findByCode(saved.second)
                _uiState.update { it.copy(fromCurrency = from, toCurrency = to) }
            } else {
                // Defaults: USD → EUR
                _uiState.update {
                    it.copy(
                        fromCurrency = CurrencyCatalog.findByCode("USD"),
                        toCurrency = CurrencyCatalog.findByCode("EUR")
                    )
                }
            }
        }
    }

    private fun loadRates(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val hasData = _uiState.value.rates != null
            if (hasData) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }
            getLatestRates(forceRefresh)
                .onSuccess { rates ->
                    _uiState.update { state ->
                        state.copy(
                            rates = rates,
                            isLoading = false,
                            isRefreshing = false,
                            isOffline = rates.isFromCache
                        )
                    }
                    recalculate()
                }
                .onFailure { error ->
                    SwissKitLogger.e("Converter", "Error cargando rates", error)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = "Sin conexión y sin caché disponible"
                        )
                    }
                }
        }
    }

    private fun observeAmountWithDebounce() {
        _amountFlow
            .debounce(400L)
            .distinctUntilChanged()
            .onEach { recalculate() }
            .launchIn(viewModelScope)
    }

    private fun recalculate() {
        val state = _uiState.value
        val rates = state.rates ?: return
        val from = state.fromCurrency ?: return
        val to = state.toCurrency ?: return
        val amount = state.amountInput.toDoubleOrNull() ?: run {
            _uiState.update { it.copy(convertedResult = "") }
            return
        }

        // Conversión mediante base USD:
        // amount (in "from") → USD → "to"
        val fromRate = rates.values[from.code] ?: 1.0
        val toRate = rates.values[to.code] ?: 1.0
        val result = amount * (toRate / fromRate)

        _uiState.update { it.copy(convertedResult = formatResult(result, to)) }
    }

    /**
     * Formatea el resultado con el símbolo de la moneda destino y máximo 4 decimales.
     * Equivalente al CurrencyFormatter del iOS.
     */
    private fun formatResult(value: Double, currency: Currency): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        val df = DecimalFormat("#,##0.####", symbols)
        return "${currency.symbol} ${df.format(value)}"
    }

    private fun persistCurrencySelection() {
        val state = _uiState.value
        val from = state.fromCurrency?.code ?: return
        val to = state.toCurrency?.code ?: return
        viewModelScope.launch {
            ratesRepository.saveSelectedCurrencies(from, to)
        }
    }
}
