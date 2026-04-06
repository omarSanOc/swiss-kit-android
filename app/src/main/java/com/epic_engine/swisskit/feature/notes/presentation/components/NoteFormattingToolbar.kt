package com.epic_engine.swisskit.feature.notes.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.feature.notes.presentation.theme.NotesDesignTokens

@Composable
fun NoteFormattingToolbar(
    onBold: () -> Unit,
    onItalic: () -> Unit,
    onBullet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DesignTokens.dimensSmall)),
        shape = RoundedCornerShape(DesignTokens.dimensSmall),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.60f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DesignTokens.dimensMedium, vertical = NotesDesignTokens.dimensSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(NotesDesignTokens.dimensSmall)
        ) {
            FormatTextButton(
                label = "B",
                fontWeight = FontWeight.Bold,
                contentDescription = "Bold",
                onClick = onBold
            )
            FormatTextButton(
                label = "I",
                fontStyle = FontStyle.Italic,
                contentDescription = "Italic",
                onClick = onItalic
            )
            FormatTextButton(
                label = "•",
                contentDescription = "Bullet list",
                onClick = onBullet
            )
        }
    }
}

@Composable
private fun FormatTextButton(
    label: String,
    contentDescription: String,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.defaultMinSize(minWidth = NotesDesignTokens.dimensLarge, minHeight = NotesDesignTokens.dimensLarge),
        contentPadding = PaddingValues(horizontal = DesignTokens.dimensXSmall, vertical = DesignTokens.dimensXXXSmall),
        shape = RoundedCornerShape(DesignTokens.dimensXXSmall),
        border = BorderStroke(NotesDesignTokens.dimensXXXSmall, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = fontWeight,
                fontStyle = fontStyle
            )
        )
    }
}