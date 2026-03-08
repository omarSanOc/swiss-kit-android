package com.epic_engine.swisskit.feature.converter.presentation.unit

import androidx.lifecycle.ViewModel
import com.epic_engine.swisskit.feature.converter.domain.model.UnitCatalog
import com.epic_engine.swisskit.feature.converter.domain.model.UnitCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class UnitConverterViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UnitConverterUiState())
    val uiState: StateFlow<UnitConverterUiState> = _uiState.asStateFlow()

    fun onEvent(event: UnitConverterEvent) {
        when (event) {
            is UnitConverterEvent.CategorySelected -> {
                val units = UnitCatalog.unitsFor(event.category)
                _uiState.update {
                    it.copy(
                        selectedCategory = event.category,
                        fromUnit = units.first(),
                        toUnit = if (units.size > 1) units[1] else units.first(),
                        amountInput = "",
                        convertedResult = ""
                    )
                }
            }
            is UnitConverterEvent.FromUnitSelected -> {
                _uiState.update { it.copy(fromUnit = event.unit) }
                recalculate()
            }
            is UnitConverterEvent.ToUnitSelected -> {
                _uiState.update { it.copy(toUnit = event.unit) }
                recalculate()
            }
            is UnitConverterEvent.AmountChanged -> {
                _uiState.update { it.copy(amountInput = event.input) }
                recalculate()
            }
            is UnitConverterEvent.SwapUnits -> {
                _uiState.update { it.copy(fromUnit = it.toUnit, toUnit = it.fromUnit) }
                recalculate()
            }
        }
    }

    private fun recalculate() {
        val state = _uiState.value
        val amount = state.amountInput.toDoubleOrNull() ?: run {
            _uiState.update { it.copy(convertedResult = "") }
            return
        }

        val result = UnitCatalog.convert(amount, state.fromUnit, state.toUnit)
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        val df = DecimalFormat("#,##0.######", symbols)
        _uiState.update { it.copy(convertedResult = "${df.format(result)} ${state.toUnit.symbol}") }
    }
}
