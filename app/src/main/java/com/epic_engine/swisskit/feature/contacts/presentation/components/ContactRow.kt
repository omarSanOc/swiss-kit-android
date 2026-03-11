package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.presentation.ContactsDesignTokens

@Composable
fun ContactRow(
    contact: Contact,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) ContactsDesignTokens.Background
    else MaterialTheme.colorScheme.surface

    ListItem(
        modifier = modifier
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .background(containerColor),
        leadingContent = {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(checkedColor = ContactsDesignTokens.Primary)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ContactsDesignTokens.Background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = ContactsDesignTokens.Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        headlineContent = {
            Text(text = contact.name, fontWeight = FontWeight.Medium)
        },
        supportingContent = {
            Text(
                text = contact.phone,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            if (!isSelectionMode) {
                Row {
                    IconButton(onClick = onCall) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Llamar",
                            tint = ContactsDesignTokens.ActionCall
                        )
                    }
                    IconButton(onClick = onWhatsApp) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "WhatsApp",
                            tint = ContactsDesignTokens.ActionWhatsApp
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            "Editar",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    )
    HorizontalDivider()
}
