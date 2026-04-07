package com.epic_engine.swisskit.feature.converter.domain.model

import androidx.annotation.StringRes
import com.epic_engine.swisskit.R

/**
 * Equivalente a las categorías de Foundation Measurement API del módulo iOS.
 * Conversión mediante factores al sistema SI.
 */
enum class UnitCategory(@StringRes val displayNameRes: Int) {
    Length(R.string.converter_category_length),
    Weight(R.string.converter_category_weight),
    Volume(R.string.converter_category_volume);

    companion object {
        val all: List<UnitCategory> = entries
    }
}

data class MeasurementUnit(
    @StringRes val nameRes: Int,
    val symbol: String,
    val toBaseFactor: Double   // Factor multiplicador para convertir a la unidad base del sistema SI
)

object UnitCatalog {

    val lengthUnits: List<MeasurementUnit> = listOf(
        MeasurementUnit(R.string.converter_unit_millimeter,  "mm",  0.001),
        MeasurementUnit(R.string.converter_unit_centimeter,  "cm",  0.01),
        MeasurementUnit(R.string.converter_unit_meter,       "m",   1.0),
        MeasurementUnit(R.string.converter_unit_kilometer,   "km",  1_000.0),
        MeasurementUnit(R.string.converter_unit_inch,        "in",  0.0254),
        MeasurementUnit(R.string.converter_unit_foot,        "ft",  0.3048),
        MeasurementUnit(R.string.converter_unit_yard,        "yd",  0.9144),
        MeasurementUnit(R.string.converter_unit_mile,        "mi",  1_609.344)
    )

    val weightUnits: List<MeasurementUnit> = listOf(
        MeasurementUnit(R.string.converter_unit_milligram,   "mg",  0.000_001),
        MeasurementUnit(R.string.converter_unit_gram,        "g",   0.001),
        MeasurementUnit(R.string.converter_unit_kilogram,    "kg",  1.0),
        MeasurementUnit(R.string.converter_unit_ton,         "t",   1_000.0),
        MeasurementUnit(R.string.converter_unit_pound,       "lb",  0.453_592),
        MeasurementUnit(R.string.converter_unit_ounce,       "oz",  0.028_349_5)
    )

    val volumeUnits: List<MeasurementUnit> = listOf(
        MeasurementUnit(R.string.converter_unit_milliliter,  "ml",     0.001),
        MeasurementUnit(R.string.converter_unit_liter,       "L",      1.0),
        MeasurementUnit(R.string.converter_unit_fl_oz,       "fl oz",  0.029_573_5),
        MeasurementUnit(R.string.converter_unit_cup,         "cup",    0.236_588),
        MeasurementUnit(R.string.converter_unit_pint,        "pt",     0.473_176),
        MeasurementUnit(R.string.converter_unit_quart,       "qt",     0.946_353),
        MeasurementUnit(R.string.converter_unit_gallon,      "gal",    3.785_41)
    )

    fun unitsFor(category: UnitCategory): List<MeasurementUnit> = when (category) {
        UnitCategory.Length -> lengthUnits
        UnitCategory.Weight -> weightUnits
        UnitCategory.Volume -> volumeUnits
    }

    /**
     * Convierte [value] de [from] a [to] dentro de la misma categoría.
     * Algoritmo: valor → unidad base SI → unidad destino
     */
    fun convert(value: Double, from: MeasurementUnit, to: MeasurementUnit): Double {
        if (value == 0.0) return 0.0
        val baseValue = value * from.toBaseFactor
        return baseValue / to.toBaseFactor
    }
}
