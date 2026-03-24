package com.epic_engine.swisskit.feature.finance.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens

@Composable
fun FinanceToggleButton(
    isActive: Boolean,
    onClick: () -> Unit,
    icon: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(FinanceDesignTokens.fieldRadius),
        color = if (isActive) FinanceDesignTokens.primaryBlue
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = if (isActive) Color.White else FinanceDesignTokens.primaryBlue,
            modifier = Modifier
                .size(FinanceDesignTokens.filterButtonHeight)
                .padding(12.dp)
        )
    }
}
