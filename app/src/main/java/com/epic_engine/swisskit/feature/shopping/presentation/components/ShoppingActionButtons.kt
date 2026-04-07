package com.epic_engine.swisskit.feature.shopping.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.feature.shopping.presentation.theme.ShoppingDesignTokens

@Composable
fun ShoppingActionButtons(
    checkedCount: Int,
    onUncheckAll: () -> Unit,
    onDeleteChecked: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = checkedCount > 0,
        enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { -it },
        exit = fadeOut(tween(250)) + slideOutVertically(tween(250)) { -it }
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(DesignTokens.dimensXSmall)
        ) {
            ActionButton(
                text = stringResource(R.string.shopping_uncheck_all),
                count = checkedCount,
                onClick = onUncheckAll
            )
            ActionButton(
                text = stringResource(R.string.shopping_delete_marked),
                count = checkedCount,
                onClick = onDeleteChecked
            )
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    count: Int,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(DesignTokens.dimensSmall),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
        border = BorderStroke(ShoppingDesignTokens.dimensXXXSmall, Color.Black.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = ShoppingDesignTokens.dimensXMedium)
                .heightIn(min = ShoppingDesignTokens.dimensLarge),
            horizontalArrangement = Arrangement.spacedBy(ShoppingDesignTokens.dimensMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "($count)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
