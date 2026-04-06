package com.epic_engine.swisskit.feature.notes.presentation.utils

import com.epic_engine.swisskit.feature.notes.domain.model.Note

/**
 * Converts internal Markdown syntax to WhatsApp-compatible format.
 *
 * Internal → WhatsApp:
 *  **bold**  →  *bold*
 *  *italic*  →  _italic_
 *  - bullet  →  • bullet
 *
 * Uses a placeholder to avoid converting bold markers twice when
 * processing italic in the second pass.
 */
fun String.toWhatsAppFormat(): String {
    val boldPlaceholder = "\u0001"
    // Pass 1 – protect bold spans (** … **) by replacing with a placeholder
    var result = Regex("""\*\*(.+?)\*\*""", RegexOption.DOT_MATCHES_ALL)
        .replace(this) { "$boldPlaceholder${it.groupValues[1]}$boldPlaceholder" }
    // Pass 2 – convert remaining single-asterisk italic to _underscore_
    result = Regex("""\*(.+?)\*""", RegexOption.DOT_MATCHES_ALL)
        .replace(result) { "_${it.groupValues[1]}_" }
    // Pass 3 – restore bold placeholders with WhatsApp bold (single asterisk)
    result = Regex("""\u0001(.+?)\u0001""", RegexOption.DOT_MATCHES_ALL)
        .replace(result) { "*${it.groupValues[1]}*" }
    // Pass 4 – convert Markdown bullets ("- text") to Unicode bullet ("• text")
    result = result.lines().joinToString("\n") { line ->
        if (line.startsWith("- ")) "• ${line.removePrefix("- ")}" else line
    }
    return result
}


fun Note.displayTitle(): String {
    if (title.isNotBlank()) return title
    val firstLine = content.lineSequence().firstOrNull { it.isNotBlank() } ?: ""
    return firstLine.take(60).ifBlank { "Sin titulo" }
}

fun Note.previewText(): String {
    val plain = content
        .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
        .replace(Regex("\\*(.*?)\\*"), "$1")
        .replace(Regex("^-\\s", RegexOption.MULTILINE), "• ")
        .replace("\n", " ")
        .trim()
    return plain.take(100)
}