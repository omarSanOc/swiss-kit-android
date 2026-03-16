package com.epic_engine.swisskit.feature.notes.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.presentation.NotesColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Single note row card — glassmorphism style using [SwissKitCard].
 * Layout: title + date on row, then content preview below.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteRowCard(
    note: Note,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayTitle = note.displayTitle()
    val displayPreview = note.previewText()
    val displayDate = formatDate(note.updatedAt)
    val a11yLabel = "$displayTitle. $displayPreview. $displayDate"

    SwissKitCard(
        modifier = modifier
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
            }
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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    if (note.reminderAt != null) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Tiene recordatorio",
                            tint = NotesColors.Purple,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(2.dp))
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
                        .padding(start = 8.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = NotesColors.Purple,
                        uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun Note.displayTitle(): String {
    if (title.isNotBlank()) return title
    val firstLine = content.lineSequence().firstOrNull { it.isNotBlank() } ?: ""
    return firstLine.take(60).ifBlank { "Sin titulo" }
}

private fun Note.previewText(): String {
    val plain = content
        .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
        .replace(Regex("\\*(.*?)\\*"), "$1")
        .replace(Regex("^-\\s", RegexOption.MULTILINE), "• ")
        .replace("\n", " ")
        .trim()
    return plain.take(100)
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}
