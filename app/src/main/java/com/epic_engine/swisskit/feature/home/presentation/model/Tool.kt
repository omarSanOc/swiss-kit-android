package com.epic_engine.swisskit.feature.home.presentation.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.epic_engine.swisskit.navigation.SwissKitDestination

/**
 * Equivalente al Tool.swift de iOS.
 * Contiene tipos de UI (DrawableRes, Color) por lo que vive en la capa de presentación.
 */
data class Tool(
    val id: String,
    val name: String,
    val description: String,
    @DrawableRes val icon: Int,
    val color: Color,
    val colorBackground: Color,
    val destination: SwissKitDestination
)
