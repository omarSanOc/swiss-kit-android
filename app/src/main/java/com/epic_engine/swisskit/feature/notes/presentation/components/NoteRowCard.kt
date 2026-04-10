package com.epic_engine.swisskit.feature.notes.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.presentation.theme.NotesDesignTokens
import com.epic_engine.swisskit.feature.notes.presentation.utils.displayTitle
import com.epic_engine.swisskit.feature.notes.presentation.utils.previewText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Single note row card — glassmorphism style using [SwissKitCard].
 * Layout: title + date on row, then content preview below.
 * Supports swipe-to-reveal with a single delete button.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteRowCard(
    note: Note,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    isRevealed: Boolean,
    onRevealChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayTitle = note.displayTitle()
    val displayPreview = note.previewText()
    val displayDate = formatDate(note.updatedAt)
    val a11yLabel = "$displayTitle. $displayPreview. $displayDate"
    val titleColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val density = LocalDensity.current
    val actionButtonsWidthPx = with(density) { DesignTokens.dimensXXLarge.toPx() }
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
        // Background layer: delete button
        IconButton(
            onClick = {
                onDelete()
                closeSwipe()
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = DesignTokens.dimensXXXSmall)
                .size(NotesDesignTokens.dimensXLarge)
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

        // Foreground layer: note card
        SwissKitCard(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .then(
                    if (!isSelectionMode) {
                        Modifier.draggable(
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
                                        offsetX.animateTo(
                                            -actionButtonsWidthPx,
                                            tween(250, easing = EaseInOut)
                                        )
                                        onRevealChange(true)
                                    } else {
                                        offsetX.animateTo(0f, tween(250, easing = EaseInOut))
                                        onRevealChange(false)
                                    }
                                }
                            }
                        )
                    } else Modifier
                )
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .semantics {
                    contentDescription = a11yLabel
                    customActions = listOf(
                        CustomAccessibilityAction("Abrir") { onClick(); true },
                        CustomAccessibilityAction("Eliminar") { onLongClick(); true }
                    )
                },
            contentPadding = PaddingValues(DesignTokens.dimensXXMedium)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Content column (title row + preview)
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.fillMaxWidth(if (isSelectionMode) 0.85f else 1f)
                ) {
                    // Top row: title + spacer + date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(DesignTokens.dimensXSmall))
                        if (note.reminderAt != null) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = "Tiene recordatorio",
                                tint = NotesDesignTokens.Primary,
                                modifier = Modifier.size(DesignTokens.dimensSmall)
                            )
                            Spacer(Modifier.width(DesignTokens.dimensXXXXSmall))
                        }
                        Text(
                            text = displayDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 1
                        )
                    }

                    // Preview
                    Text(
                        text = displayPreview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Selection checkbox (top-end)
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(start = DesignTokens.dimensXSmall),
                        colors = CheckboxDefaults.colors(
                            checkedColor = NotesDesignTokens.Primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }
    }
}
private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}
