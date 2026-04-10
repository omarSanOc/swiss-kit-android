package com.epic_engine.swisskit.feature.home.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.epic_engine.swisskit.navigation.SwissKitDestination

/**
 * Represents a single app module entry shown on the Home screen.
 * Contains UI types (DrawableRes, Color) and therefore lives in the presentation layer.
 */
data class Tool(
    val id: String,
    @StringRes val name: Int,
    @StringRes val description: Int,
    @DrawableRes val icon: Int,
    val color: Color,
    val backgroundColor: Color,
    val destination: SwissKitDestination
)
