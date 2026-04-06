package com.epic_engine.swisskit.feature.notes.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object NotesDesignTokens {
    val Primary = Color(0xFF6A5ACD)             // Color primario/acento (SlateBlue)
    val background = Color(0xFFEFEAFF)        // Gradiente final light mode
    val darkBackground = Color(0xFF4E4C54)         // Gradiente final dark mode
    val accent = Color(0xFFB7ADFF)   // Acento en dark mode (reminder sheet)
    val FABGradientTop = Color(0xFF7361F2)     // Parte superior gradiente FAB
    val FABGradientBottom = Color(0xFF6A5ACD)  // Parte inferior gradiente FAB
    val CardShadow = Color.Black.copy(alpha = 0.08f)
    val ReminderSheetLight = Color(0xFFEFEAFF).copy(alpha = 0.96f)
    val ReminderSheetDark = Color(0xFF3B3746).copy(alpha = 0.96f)

    val CardBackground = Color(0xFFF8F6FF)
    val PreviewBackground = Color(0xFFF3F0FF)

    val dimensSmall = 10.dp
    val dimensXXSmall = 3.dp
    val dimensXXXSmall = 1.dp
    val dimensXXLarge = 68.dp
    val dimensXLarge = 44.dp

    val dimensLarge = 32.dp
}

