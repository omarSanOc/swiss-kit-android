package com.epic_engine.swisskit.feature.converter.domain.model

/**
 * Equivalente a las categorías de Foundation Measurement API del módulo iOS.
 * Conversión mediante factores al sistema SI.
 */
sealed class UnitCategory(val displayName: String) {

    data object Length : UnitCategory("Longitud")
    data object Weight : UnitCategory("Peso")
    data object Volume : UnitCategory("Volumen")

    companion object {
        val all: List<UnitCategory> = listOf(Length, Weight, Volume)
    }
}

data class MeasurementUnit(
    val name: String,
    val symbol: String,
    val toBaseFactor: Double   // Factor multiplicador para convertir a la unidad base del sistema SI
)

object UnitCatalog {

    val lengthUnits: List<MeasurementUnit> = listOf(
        MeasurementUnit("Milímetro",  "mm",  0.001),
        MeasurementUnit("Centímetro", "cm",  0.01),
        MeasurementUnit("Metro",      "m",   1.0),
        MeasurementUnit("Kilómetro",  "km",  1_000.0),
        MeasurementUnit("Pulgada",    "in",  0.0254),
        MeasurementUnit("Pie",        "ft",  0.3048),
        MeasurementUnit("Yarda",      "yd",  0.9144),
        MeasurementUnit("Milla",      "mi",  1_609.344)
    )

    val weightUnits: List<MeasurementUnit> = listOf(
        MeasurementUnit("Miligramo",  "mg",  0.000_001),
        MeasurementUnit("Gramo",      "g",   0.001),
        MeasurementUnit("Kilogramo",  "kg",  1.0),
        MeasurementUnit("Tonelada",   "t",   1_000.0),
        MeasurementUnit("Libra",      "lb",  0.453_592),
        MeasurementUnit("Onza",       "oz",  0.028_349_5)
    )

    val volumeUnits: List<MeasurementUnit> = listOf(
        MeasurementUnit("Mililitro",   "ml",     0.001),
        MeasurementUnit("Litro",       "L",      1.0),
        MeasurementUnit("Fl. Onza",    "fl oz",  0.029_573_5),
        MeasurementUnit("Taza",        "cup",    0.236_588),
        MeasurementUnit("Pinta",       "pt",     0.473_176),
        MeasurementUnit("Cuarto",      "qt",     0.946_353),
        MeasurementUnit("Galón",       "gal",    3.785_41)
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
