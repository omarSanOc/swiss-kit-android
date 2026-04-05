package com.epic_engine.swisskit.feature.finance.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinanceItemRow(
    item: Finance,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    isRevealed: Boolean,
    onRevealChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDeleteRequest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX")) }
    val isIncome = item.type == FinanceType.INCOME
    val amountColor = if (isIncome) FinanceDesignTokens.incomeColor else FinanceDesignTokens.expenseColor
    val itemDescription = "${if (isIncome) "Ingreso" else "Gasto"}: ${item.title}"
    val density = LocalDensity.current
    val actionButtonsWidthPx = with(density) { DesignTokens.dimensXXLarge.toPx() }
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

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
            .clip(RoundedCornerShape(DesignTokens.dimensXXXMedium))
            .semantics { contentDescription = itemDescription }
    ) {
        // Back layer: delete button
        IconButton(
            onClick = {
                onDeleteRequest()
                closeSwipe()
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = DesignTokens.dimensXXSmall)
                .size(FinanceDesignTokens.dimensXXLarge)
                .clip(CircleShape)
                .background(DesignTokens.deleteColor)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar transacción",
                tint = Color.White,
                modifier = Modifier.size(DesignTokens.dimensXXMedium)
            )
        }

        // Front layer: card + selection indicator overlay
        Box {
            SwissKitCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isSelectionMode && isSelected)
                            Modifier.border(DesignTokens.dimensXXXXSmall, FinanceDesignTokens.primary, RoundedCornerShape(DesignTokens.dimensXXXMedium))
                        else Modifier
                    )
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .draggable(
                        enabled = !isSelectionMode,
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            scope.launch {
                                val newValue = (offsetX.value + delta).coerceIn(-actionButtonsWidthPx, 0f)
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
                    )
                    .clickable(onClick = onClick),
                contentPadding = PaddingValues(DesignTokens.dimensXSmall)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignTokens.dimensMedium, vertical = DesignTokens.dimensSmall),
                    verticalArrangement = Arrangement.spacedBy(FinanceDesignTokens.dimensXXXXSmall)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dateFormat.format(Date(item.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.55f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(DesignTokens.dimensSmall),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                                modifier = Modifier.padding(horizontal = DesignTokens.dimensXXSmall, vertical = DesignTokens.dimensXXXXSmall)
                            )
                        }
                        Text(
                            text = if (isIncome) currencyFormat.format(item.amount)
                                   else "-${currencyFormat.format(item.amount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = amountColor
                        )
                    }
                }
            }

            if (isSelectionMode) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.CheckCircle
                                  else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) FinanceDesignTokens.primary
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                        .padding(top = FinanceDesignTokens.dimensSmall, end = FinanceDesignTokens.dimensSmall)
                        .size(FinanceDesignTokens.dimensMedium)
                )
            }
        }
    }
}
