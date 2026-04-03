package com.epic_engine.swisskit.feature.converter.presentation.currency

import com.epic_engine.swisskit.feature.converter.domain.model.Currency
import com.epic_engine.swisskit.feature.converter.domain.model.Rates

data class CurrencyConverterUiState(
    val rates: Rates? = null,
    val fromCurrency: Currency? = null,
    val toCurrency: Currency? = null,
    val amountInput: String = "",
    val convertedResult: String = "",
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,      // true cuando se usa cache stale
    val errorMessage: String? = null,
    val showCopiedToast: Boolean = false
) {
    val isResultAvailable: Boolean get() = convertedResult.isNotBlank() && rates != null
    val canConvert: Boolean get() = fromCurrency != null && toCurrency != null && rates != null
}

sealed class CurrencyConverterEvent {
    data class AmountChanged(val input: String) : CurrencyConverterEvent()
    data class FromCurrencySelected(val currency: Currency) : CurrencyConverterEvent()
    data class ToCurrencySelected(val currency: Currency) : CurrencyConverterEvent()
    data object SwapCurrencies : CurrencyConverterEvent()
    data object Refresh : CurrencyConverterEvent()
    data object ClearError : CurrencyConverterEvent()
    data object ShowCopiedToast : CurrencyConverterEvent()
    data object DismissCopiedToast : CurrencyConverterEvent()
}
