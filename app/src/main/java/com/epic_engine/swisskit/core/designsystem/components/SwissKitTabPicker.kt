package com.epic_engine.swisskit.core.designsystem.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val tabTrackLight        = Color(0xFF000000).copy(alpha = 0.07f)
private val tabPillLight         = Color.White
private val tabBorderLight       = Color(0xFF000000).copy(alpha = 0.10f)
private val tabTextActiveLight   = Color(0xFF111827)
private val tabTextInactiveLight = Color(0xFF2A2929)

private val tabTrackDark         = Color(0xFF000000).copy(alpha = 0.30f)
private val tabPillDark          = Color(0xFF52525B)
private val tabBorderDark        = Color.White.copy(alpha = 0.13f)
private val tabTextActiveDark    = Color.White
private val tabTextInactiveDark  = Color.White.copy(alpha = 0.60f)

private val tabPickerHeight       = 44.dp
private val tabPickerPadding      = 6.dp
private val tabPickerOuterRadius  = 18.dp
private val tabPickerInnerRadius  = 16.dp

@Composable
fun SwissKitTabPicker(
    options: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (options.isEmpty()) return

    val safeIndex = selectedIndex.coerceIn(0, options.lastIndex)
    val isDark = isSystemInDarkTheme()

    val trackColor   = if (isDark) tabTrackDark        else tabTrackLight
    val pillColor    = if (isDark) tabPillDark          else tabPillLight
    val borderColor  = if (isDark) tabBorderDark        else tabBorderLight
    val textActive   = if (isDark) tabTextActiveDark    else tabTextActiveLight
    val textInactive = if (isDark) tabTextInactiveDark  else tabTextInactiveLight

    val trackShape = RoundedCornerShape(tabPickerOuterRadius)
    val pillShape  = RoundedCornerShape(tabPickerInnerRadius)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(tabPickerHeight)
            .clip(trackShape)
            .background(trackColor, trackShape)
            .border(BorderStroke(0.5.dp, borderColor), trackShape)
            .padding(tabPickerPadding)
    ) {
        val pillWidth = maxWidth / options.size

        val pillOffsetX by animateDpAsState(
            targetValue = pillWidth * safeIndex,
            animationSpec = tween(durationMillis = 250),
            label = "pillOffset"
        )

        Box(
            modifier = Modifier
                .offset(x = pillOffsetX)
                .width(pillWidth)
                .fillMaxHeight()
                .shadow(1.dp, pillShape)
                .background(pillColor, pillShape)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            options.forEachIndexed { index, label ->
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
                        fontWeight = if (safeIndex == index) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (safeIndex == index) textActive else textInactive
                    )
                }
            }
        }
    }
}
