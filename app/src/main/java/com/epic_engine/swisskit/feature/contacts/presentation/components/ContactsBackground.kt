package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTeal
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTealDark
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTealLight

@Composable
fun ContactsBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val colors = if (isDark) listOf(ContactsTeal, ContactsTealDark)
                 else listOf(ContactsTeal, ContactsTealLight)

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
