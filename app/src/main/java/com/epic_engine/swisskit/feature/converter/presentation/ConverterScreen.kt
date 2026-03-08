package com.epic_engine.swisskit.feature.converter.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.feature.converter.presentation.currency.CurrencyConverterContent
import com.epic_engine.swisskit.feature.converter.presentation.currency.CurrencyConverterEvent
import com.epic_engine.swisskit.feature.converter.presentation.currency.CurrencyConverterViewModel
import com.epic_engine.swisskit.feature.converter.presentation.unit.UnitConverterContent
import com.epic_engine.swisskit.feature.converter.presentation.unit.UnitConverterViewModel
import com.epic_engine.swisskit.ui.theme.grayConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    currencyViewModel: CurrencyConverterViewModel = hiltViewModel(),
    unitViewModel: UnitConverterViewModel = hiltViewModel()
) {
    val currencyUiState by currencyViewModel.uiState.collectAsStateWithLifecycle()
    val unitUiState by unitViewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Divisas", "Unidades")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Converter",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    if (selectedTab == 0) {
                        IconButton(onClick = { currencyViewModel.onEvent(CurrencyConverterEvent.Refresh) }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualizar tasas",
                                tint = grayConverter
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = grayConverter
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) grayConverter
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> CurrencyConverterContent(
                    uiState = currencyUiState,
                    onEvent = currencyViewModel::onEvent
                )
                1 -> UnitConverterContent(
                    uiState = unitUiState,
                    onEvent = unitViewModel::onEvent
                )
            }
        }
    }
}
