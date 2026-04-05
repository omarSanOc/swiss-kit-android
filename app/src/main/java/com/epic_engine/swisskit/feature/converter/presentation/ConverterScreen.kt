package com.epic_engine.swisskit.feature.converter.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.core.designsystem.components.SwissKitBackground
import com.epic_engine.swisskit.core.designsystem.components.SwissKitTabPicker
import com.epic_engine.swisskit.feature.converter.presentation.components.CurrencyConverterContent
import com.epic_engine.swisskit.feature.converter.presentation.components.UnitConverterContent
import com.epic_engine.swisskit.feature.converter.presentation.theme.ConverterDesignTokens
import com.epic_engine.swisskit.feature.converter.presentation.viewmodel.CurrencyConverterViewModel
import com.epic_engine.swisskit.feature.converter.presentation.viewmodel.UnitConverterViewModel

@Composable
fun ConverterScreen(
    currencyViewModel: CurrencyConverterViewModel = hiltViewModel(),
    unitViewModel: UnitConverterViewModel = hiltViewModel()
) {
    val currencyUiState by currencyViewModel.uiState.collectAsStateWithLifecycle()
    val unitUiState by unitViewModel.uiState.collectAsStateWithLifecycle()

    // Índice 0 = Unidades, índice 1 = Divisas (alineado con iOS)
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    SwissKitBackground(
        colors = listOf(ConverterDesignTokens.gradientStart, ConverterDesignTokens.gradientEnd),
        darkColors = listOf(ConverterDesignTokens.gradientStart, ConverterDesignTokens.gradientDarkEnd),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Toolbar custom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ConverterDesignTokens.dimensXXLarge)
                        .padding(horizontal = ConverterDesignTokens.dimensMedium)
                ) {
                    Text(
                        text = "Conversor",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }

                // Tab picker
                SwissKitTabPicker(
                    options = listOf("Unidades", "Divisas"),
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ConverterDesignTokens.dimensMedium)
                )

                Spacer(modifier = Modifier.height(ConverterDesignTokens.dimensXSmall))

                // Contenido según tab
                when (selectedTab) {
                    0 -> UnitConverterContent(
                        uiState = unitUiState,
                        onEvent = unitViewModel::onEvent
                    )
                    1 -> CurrencyConverterContent(
                        uiState = currencyUiState,
                        onEvent = currencyViewModel::onEvent
                    )
                }
            }
        }
    )
}
