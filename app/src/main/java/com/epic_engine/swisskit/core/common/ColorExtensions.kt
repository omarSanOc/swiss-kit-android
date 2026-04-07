package com.epic_engine.swisskit.core.common

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

/**
 * Crea un Color desde una cadena hexadecimal.
 * Soporta formatos: "#RRGGBB", "#AARRGGBB", "RRGGBB", "AARRGGBB"
 */
fun Color(hex: String): Color {
    val sanitized = hex.trimStart('#')
    val value = when (sanitized.length) {
        6 -> "FF$sanitized"
        8 -> sanitized
        else -> throw IllegalArgumentException("Color hex invalido: $hex")
    }
    return Color("#$value".toColorInt())
}
