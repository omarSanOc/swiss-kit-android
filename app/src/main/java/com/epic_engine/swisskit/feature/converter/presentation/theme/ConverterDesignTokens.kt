package com.epic_engine.swisskit.feature.converter.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ConverterDesignTokens {

    // ═══════════════ COLORS ═══════════════
    val accentBlue = Color(0xFF0080FF)
    val cardBackground = Color.White
    val borderColor = Color(0xFFB0B0B0)
    val gradientStart = Color(0xFF6B7280)
    val gradientEnd = Color(0xFFF3F4F6)
    val gradientDarkEnd = Color(0xFF49494A)

    // ═══════════════ TAB PICKER — LIGHT ═══════════════
    val tabTrackLight        = Color(0xFF000000).copy(alpha = 0.07f)
    val tabPillLight         = Color.White
    val tabBorderLight       = Color(0xFF000000).copy(alpha = 0.10f)
    val tabTextActiveLight   = Color(0xFF111827)
    val tabTextInactiveLight = Color(0xFF2A2929)

    // ═══════════════ TAB PICKER — DARK ═══════════════
    val tabTrackDark         = Color(0xFF000000).copy(alpha = 0.30f)
    val tabPillDark          = Color(0xFF52525B)
    val tabBorderDark        = Color.White.copy(alpha = 0.13f)
    val tabTextActiveDark    = Color.White
    val tabTextInactiveDark  = Color.White.copy(alpha = 0.60f)

    // ═══════════════ SPACING ═══════════════
    val sectionSpacing = 24.dp
    val cardInternalPadding = 20.dp
    val fieldSpacing = 12.dp
    val screenHorizontalPadding = 16.dp
    val toolbarHeight = 56.dp

    // ═══════════════ RADIUS ═══════════════
    val cardCornerRadius = 20.dp
    val fieldCornerRadius = 18.dp
    val tabPickerCornerRadius = 18.dp
    val tabPickerInnerRadius = 16.dp

    // ═══════════════ SIZES ═══════════════
    val pickerMinHeight = 48.dp
    val tabPickerHeight = 44.dp
    val tabPickerPadding = 6.dp

    // ═══════════════ STROKE ═══════════════
    val fieldBorderWidth = 0.5.dp

    // ═══════════════ ELEVATION ═══════════════
    val cardShadowElevation = 4.dp
    val fieldShadowElevation = 2.dp

    // ═══════════════ OPACITY ═══════════════
    val borderAlpha = 0.6f
    val tabPickerBgAlpha = 0.3f
    val tabPickerBorderAlpha = 0.28f
}
