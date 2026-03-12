package com.epic_engine.swisskit.feature.home.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.epic_engine.swisskit.navigation.SwissKitDestination

/**
 * Equivalente al Tool.swift de iOS.
 * Contiene tipos de UI (ImageVector, Color) por lo que vive en la capa de presentación.
 */
data class Tool(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val destination: SwissKitDestination
)
