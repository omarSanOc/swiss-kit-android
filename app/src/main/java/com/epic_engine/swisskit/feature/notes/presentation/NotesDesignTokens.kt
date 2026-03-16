package com.epic_engine.swisskit.feature.notes.presentation

import androidx.compose.ui.graphics.Color

object NotesColors {
    val Purple = Color(0xFF6A5ACD)             // Color primario/acento (SlateBlue)
    val PurpleLight = Color(0xFFEFEAFF)        // Gradiente final light mode
    val PurpleDark = Color(0xFF4E4C54)         // Gradiente final dark mode
    val PurpleAccentDark = Color(0xFFB7ADFF)   // Acento en dark mode (reminder sheet)
    val FABGradientTop = Color(0xFF7361F2)     // Parte superior gradiente FAB
    val FABGradientBottom = Color(0xFF6A5ACD)  // Parte inferior gradiente FAB
    val CardShadow = Color.Black.copy(alpha = 0.08f)
    val ReminderSheetLight = Color(0xFFEFEAFF).copy(alpha = 0.96f)
    val ReminderSheetDark = Color(0xFF3B3746).copy(alpha = 0.96f)

    // Legacy aliases — kept for backward compatibility
    val Primary = Purple
    val Background = PurpleLight
    val CardBackground = Color(0xFFF8F6FF)
    val PreviewBackground = Color(0xFFF3F0FF)
}

/** Backward-compatibility alias so existing usages still compile. */
typealias NotesDesignTokens = NotesColors
