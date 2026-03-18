package com.epic_engine.swisskit.core.designsystem.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTeal
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTealDark
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTealLight

@Composable
fun SwissKitBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    colors: List<Color>,
    darkColors: List<Color>
) {
    val isDark = isSystemInDarkTheme()
    val colors = if (isDark) darkColors
                 else colors

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = colors,
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    )
                )
            }
    ) {
        content()
    }
}
