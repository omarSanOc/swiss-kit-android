package com.epic_engine.swisskit.feature.notes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView

/**
 * Empty state shown when there are no notes or no search results.
 * All text and icons are white so they stand out over the gradient background.
 */
@Composable
fun NotesEmptyState(
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SwissKitEmptyView(
                icon = R.drawable.icon_notes,
                title = "Notas",
                subtitle = if (isSearching) "Sin resultados para tu búsqueda"
                else "Escribe tus notas aqui",
                modifier = Modifier.fillMaxSize(),
                iconTint = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
