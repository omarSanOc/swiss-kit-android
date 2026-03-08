package com.epic_engine.swisskit.feature.shopping.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem
import com.epic_engine.swisskit.ui.theme.yellowShopping

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = slideInHorizontally(animationSpec = tween(200)) + fadeIn(),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        SwissKitCard(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = DesignTokens.contentPaddingMedium,
                        vertical = DesignTokens.contentPaddingSmall
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = yellowShopping,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (item.isChecked)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar ${item.name}",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
