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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = "Desmarcar",
                count = checkedCount,
                onClick = onUncheckAll
            )
            ActionButton(
                text = "Borrar marcados",
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
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .heightIn(min = 44.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
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
