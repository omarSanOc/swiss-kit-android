package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDesignTokens
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ContactRow(
    contact: Contact,
    isRevealed: Boolean,
    onRevealChange: (Boolean) -> Unit,
    onShowActionSheet: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val actionButtonsWidthPx = with(density) { ContactsDesignTokens.actionButtonsWidth.toPx() }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

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
            .clip(RoundedCornerShape(ContactsDesignTokens.cardCornerRadius))
    ) {
        // Back layer: delete button
        IconButton(
            onClick = {
                onDelete()
                closeSwipe()
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = ContactsDesignTokens.deletePadding)
                .size(ContactsDesignTokens.deleteButtonSize)
                .clip(CircleShape)
                .background(ContactsDesignTokens.ContactsDeleteAction)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Front layer: card with draggable
        SwissKitCard(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
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
                ),
            contentPadding = PaddingValues(ContactsDesignTokens.cardInternalPadding)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource( R.drawable.icon_user),
                    contentDescription = "Contacto ${contact.name}",
                    tint = ContactsDesignTokens.Primary,
                    modifier = Modifier.size(ContactsDesignTokens.rowIconSize)
                )
                Spacer(Modifier.width(ContactsDesignTokens.spacingXSmallPadding))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contact.name,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tel. ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = contact.phone,
                            style = MaterialTheme.typography.bodyMedium.merge(
                                TextStyle(textDecoration = TextDecoration.Underline)
                            ),
                            color = ContactsDesignTokens.ContactsPhoneLink,
                            modifier = Modifier.clickable(onClick = onShowActionSheet)
                        )
                    }
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            painter = painterResource( R.drawable.icon_ellipsis),
                            contentDescription = "Más opciones",
                            tint = ContactsDesignTokens.Primary,
                            modifier = Modifier.size(ContactsDesignTokens.menuIconSize)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar contacto") },
                            leadingIcon = { Icon(Icons.Default.Edit, null) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = ContactsDesignTokens.ContactsDeleteAction) },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, null, tint = ContactsDesignTokens.ContactsDeleteAction)
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
