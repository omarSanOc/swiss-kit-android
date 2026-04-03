package com.epic_engine.swisskit.feature.converter.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.converter.presentation.theme.ConverterDesignTokens

private val tabs = listOf("Unidades", "Divisas")

@Composable
fun ConverterTabPicker(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val trackColor  = if (isDark) ConverterDesignTokens.tabTrackDark  else ConverterDesignTokens.tabTrackLight
    val pillColor   = if (isDark) ConverterDesignTokens.tabPillDark   else ConverterDesignTokens.tabPillLight
    val borderColor = if (isDark) ConverterDesignTokens.tabBorderDark else ConverterDesignTokens.tabBorderLight
    val textActive  = if (isDark) ConverterDesignTokens.tabTextActiveDark  else ConverterDesignTokens.tabTextActiveLight
    val textInactive   = if (isDark) ConverterDesignTokens.tabTextInactiveDark else ConverterDesignTokens.tabTextInactiveLight

    val trackShape = RoundedCornerShape(ConverterDesignTokens.tabPickerCornerRadius)
    val pillShape  = RoundedCornerShape(ConverterDesignTokens.tabPickerInnerRadius)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(ConverterDesignTokens.tabPickerHeight)
            .clip(trackShape)
            .background(trackColor, trackShape)
            .border(BorderStroke(0.5.dp, borderColor), trackShape)
            .padding(ConverterDesignTokens.tabPickerPadding)
    ) {
        val pillWidth = maxWidth / 2

        val pillOffsetX by animateDpAsState(
            targetValue = if (selectedIndex == 0) 0.dp else pillWidth,
            animationSpec = tween(durationMillis = 250),
            label = "pillOffset"
        )

        // Pill deslizante (debajo de los labels)
        Box(
            modifier = Modifier
                .offset(x = pillOffsetX)
                .width(pillWidth)
                .fillMaxHeight()
                .shadow(1.dp, pillShape)
                .background(pillColor, pillShape)
        )

        // Zonas de tap + labels (encima de la pill)
        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEachIndexed { index, label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedIndex == index) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedIndex == index) textActive else textInactive
                    )
                }
            }
        }
    }
}
