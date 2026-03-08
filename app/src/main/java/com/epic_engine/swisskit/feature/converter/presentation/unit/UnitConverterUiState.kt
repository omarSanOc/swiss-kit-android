package com.epic_engine.swisskit.feature.converter.presentation.unit

import com.epic_engine.swisskit.feature.converter.domain.model.MeasurementUnit
import com.epic_engine.swisskit.feature.converter.domain.model.UnitCatalog
import com.epic_engine.swisskit.feature.converter.domain.model.UnitCategory

data class UnitConverterUiState(
    val selectedCategory: UnitCategory = UnitCategory.Length,
    val fromUnit: MeasurementUnit = UnitCatalog.lengthUnits.first(),
    val toUnit: MeasurementUnit = UnitCatalog.lengthUnits[2],   // metro por defecto
    val amountInput: String = "",
    val convertedResult: String = ""
) {
    val availableUnits: List<MeasurementUnit>
        get() = UnitCatalog.unitsFor(selectedCategory)
}

sealed class UnitConverterEvent {
    data class CategorySelected(val category: UnitCategory) : UnitConverterEvent()
    data class FromUnitSelected(val unit: MeasurementUnit) : UnitConverterEvent()
    data class ToUnitSelected(val unit: MeasurementUnit) : UnitConverterEvent()
    data class AmountChanged(val input: String) : UnitConverterEvent()
    data object SwapUnits : UnitConverterEvent()
}
