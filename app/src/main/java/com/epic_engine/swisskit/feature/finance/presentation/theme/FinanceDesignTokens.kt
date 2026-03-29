package com.epic_engine.swisskit.feature.finance.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object FinanceDesignTokens {

    // ═══════════════ COLORS ═══════════════
    val primaryBlue = Color(0xFF0080FF)
    val incomeGreen = Color(0xFF0FB380)
    val expenseRed = Color(0xFFFB2A2A)
    val backgroundLight = Color(0xFFE6F2FF)
    val backgroundDark = Color(0xFF27292B)
    val expenseBackground = Color(0xFFFFEBE9)
    val gradientStart = Color(0xFF5966F2)
    val cardSurface = Color.White

    // Backward-compat aliases
    val incomeColor = incomeGreen
    val expenseColor = expenseRed
    val accentBlue = primaryBlue
    val backgroundBlue = backgroundLight

    // ═══════════════ SPACING ═══════════════
    val screenOuterPadding = 24.dp
    val screenHorizontalPadding = 16.dp
    val sectionSpacing = 24.dp
    val fieldPadding = 12.dp
    val chipHPadding = 16.dp
    val chipVPadding = 10.dp
    val chipFlowSpacing = 12.dp
    val rowMetadataSpacing = 8.dp
    val listRowInset = 20.dp
    val listRowGap = 4.dp

    // ═══════════════ RADIUS ═══════════════
    val fieldRadius = 12.dp
    val typeCardRadius = 18.dp
    val chipRadius = 20.dp
    val filterPanelRadius = 20.dp
    val amountBadgeRadius = 12.dp
    val transactionCardRadius = 24.dp

    // ═══════════════ SIZES ═══════════════
    val fieldMinHeight = 48.dp
    val notesMinHeight = 96.dp
    val typeCardMinHeight = 120.dp
    val typeCardIconSize = 18.dp
    val typeCardIndicator = 22.dp
    val filterButtonHeight = 48.dp
    val toolbarIconSize = 24.dp
    val summaryCardHeight = 100.dp
    val rowIconSize = 36.dp

    // ═══════════════ STROKE ═══════════════
    val fieldBorderWidth = 0.5.dp
    val typeCardBorderWidth = 2.dp

    // ═══════════════ OPACITY ═══════════════
    val typeSelectionFillAlpha = 0.12f
    val chipTextAlpha = 0.7f
    val headerTextAlpha = 0.9f
}
