package com.epic_engine.swisskit.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.feature.home.presentation.components.HomeToolCard
import com.epic_engine.swisskit.feature.home.presentation.model.ToolCatalog
import com.epic_engine.swisskit.feature.home.presentation.theme.HomeDesignTokens
import com.epic_engine.swisskit.navigation.SwissKitDestination

@Composable
fun HomeScreen(
    onNavigateTo: (SwissKitDestination) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = DesignTokens.dimensMedium,
                end = DesignTokens.dimensMedium,
                top = HomeDesignTokens.dimensXLarge,
                bottom = HomeDesignTokens.dimensXXLarge
            ),
            verticalArrangement = Arrangement.spacedBy(HomeDesignTokens.dimensSmall),
            horizontalArrangement = Arrangement.spacedBy(HomeDesignTokens.dimensSmall)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeHeader()
            }

            items(
                items = ToolCatalog.all,
                key = { it.id }
            ) { tool ->
                HomeToolCard(
                    tool = tool,
                    onClick = { onNavigateTo(tool.destination) }
                )
            }
        }

        HomeFooter(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = HomeDesignTokens.dimensLarge)
        )
    }
}

@Composable
private fun HomeHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = DesignTokens.dimensMedium)
    ) {
        Text(
            text = "SwissKit",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = "Tu navaja suiza digital para el día a día.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun HomeFooter(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = DesignTokens.dimensMedium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hecho para simplificar tu vida",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}
