package com.epic_engine.swisskit.feature.converter.domain.model

/** Catalog of the 15 supported currencies. */
object CurrencyCatalog {

    val supported: List<Currency> = listOf(
        Currency("USD", "$"),
        Currency("EUR", "€"),
        Currency("GBP", "£"),
        Currency("JPY", "¥"),
        Currency("CNY", "¥"),
        Currency("GTQ", "Q"),
        Currency("VES", "Bs"),
        Currency("NIO", "C$"),
        Currency("BRL", "R$"),
        Currency("MXN", "$"),
        Currency("COP", "$"),
        Currency("ARS", "$"),
        Currency("CLP", "$"),
        Currency("CAD", "CA$"),
        Currency("RUB", "₽")
    )

    val supportedCodes: Set<String> = supported.map { it.code }.toSet()

    fun findByCode(code: String): Currency? = supported.find { it.code == code }

    fun symbolFor(code: String): String = findByCode(code)?.symbol ?: code
}
