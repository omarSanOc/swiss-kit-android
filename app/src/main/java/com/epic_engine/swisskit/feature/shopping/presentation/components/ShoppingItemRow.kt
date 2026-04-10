package com.epic_engine.swisskit.feature.shopping.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.feature.home.presentation.theme.HomeDesignTokens
import com.epic_engine.swisskit.feature.shopping.domain.model.ShoppingItem
import com.epic_engine.swisskit.feature.shopping.presentation.theme.ShoppingDesignTokens
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    isRevealed: Boolean,
    onRevealChange: (Boolean) -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val actionButtonsWidthPx = with(density) { ShoppingDesignTokens.dimensXXXLarge.toPx() }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isRevealed) {
        if (!isRevealed && offsetX.value != 0f) {
            offsetX.animateTo(0f, tween(250, easing = EaseInOut))
        }
    }

    fun closeSwipe() {
        scope.launch {
            offsetX.animateTo(0f, tween(250, easing = EaseInOut))
            onRevealChange(false)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTokens.dimensXXMedium))
    ) {
        // Background layer: action buttons
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = DesignTokens.dimensXXXSmall),
            horizontalArrangement = Arrangement.spacedBy(DesignTokens.dimensXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onEdit()
                    closeSwipe()
                },
                modifier = Modifier
                    .size(ShoppingDesignTokens.dimensLarge)
                    .clip(CircleShape)
                    .background(DesignTokens.editColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color.White,
                    modifier = Modifier.size(DesignTokens.dimensXXMedium)
                )
            }

            IconButton(
                onClick = {
                    onDelete()
                    closeSwipe()
                },
                modifier = Modifier
                    .size(ShoppingDesignTokens.dimensLarge)
                    .clip(CircleShape)
                    .background(DesignTokens.deleteColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White,
                    modifier = Modifier.size(DesignTokens.dimensXXMedium)
                )
            }
        }

        // Foreground layer: item card
        SwissKitCard(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            val newValue = (offsetX.value + delta)
                                .coerceIn(-actionButtonsWidthPx, 0f)
                            offsetX.snapTo(newValue)
                        }
                    },
                    onDragStopped = {
                        scope.launch {
                            val threshold = -actionButtonsWidthPx * 0.4f
                            if (offsetX.value < threshold) {
                                offsetX.animateTo(-actionButtonsWidthPx, tween(250, easing = EaseInOut))
                                onRevealChange(true)
                            } else {
                                offsetX.animateTo(0f, tween(250, easing = EaseInOut))
                                onRevealChange(false)
                            }
                        }
                    }
                ),
            contentPadding = PaddingValues(DesignTokens.dimensXXXSmall)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignTokens.dimensMedium, vertical = ShoppingDesignTokens.dimensXMedium),
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.dimensSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (item.isChecked) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = if (item.isChecked) "Desmarcar" else "Marcar",
                    tint = if (item.isChecked) ShoppingDesignTokens.Primary
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(DesignTokens.dimensXXXMedium)
                        .clip(CircleShape)
                        .clickable(onClick = onToggle)
                )

                Spacer(modifier = Modifier.width(DesignTokens.dimensXXXSmall))

                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (item.isChecked)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough
                                     else TextDecoration.None,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = DesignTokens.dimensXXSmall)
                )
            }
        }
    }
}
