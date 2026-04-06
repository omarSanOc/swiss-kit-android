package com.epic_engine.swisskit.core.designsystem.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.notes.presentation.theme.NotesDesignTokens


/** Diagonal linear gradient shared by all Notes screens. */
@Composable
fun notesBackgroundBrush(): Brush {
    val isDark = isSystemInDarkTheme()
    return Brush.linearGradient(
        colors = listOf(
            NotesDesignTokens.Primary,
            if (isDark) NotesDesignTokens.darkBackground else NotesDesignTokens.background
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )
}

/**
 * Base glassmorphism card used throughout the Notes module.
 * Simulates iOS ultraThickMaterial: surface at 82 % opacity, 20 dp corners, 8 dp elevation.
 */
@Composable
fun SwissKitCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = NotesDesignTokens.CardShadow,
                spotColor = NotesDesignTokens.CardShadow
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

