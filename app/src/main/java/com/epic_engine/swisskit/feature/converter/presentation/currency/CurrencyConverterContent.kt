package com.epic_engine.swisskit.feature.converter.presentation.currency

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.core.designsystem.components.SwissKitTextField
import com.epic_engine.swisskit.feature.converter.domain.model.CurrencyCatalog
import com.epic_engine.swisskit.feature.converter.presentation.components.CurrencyPickerDropdown
import com.epic_engine.swisskit.ui.theme.grayConverter

@Composable
fun CurrencyConverterContent(
    uiState: CurrencyConverterUiState,
    onEvent: (CurrencyConverterEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(DesignTokens.contentPaddingMedium),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.contentPaddingMedium)
    ) {
        // Indicador offline
        if (uiState.isOffline) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Usando tasas guardadas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        // Campo de monto origen
        SwissKitTextField(
            value = uiState.amountInput,
            onValueChange = { onEvent(CurrencyConverterEvent.AmountChanged(it)) },
            label = "Monto",
            accentColor = grayConverter,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // Selector de moneda origen
        CurrencyPickerDropdown(
            label = "De",
            selected = uiState.fromCurrency,
            currencies = CurrencyCatalog.supported,
            onSelected = { onEvent(CurrencyConverterEvent.FromCurrencySelected(it)) },
            accentColor = grayConverter,
            modifier = Modifier.fillMaxWidth()
        )

        // Botón swap
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { onEvent(CurrencyConverterEvent.SwapCurrencies) }) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Intercambiar monedas",
                    tint = grayConverter
                )
            }
        }

        // Selector de moneda destino
        CurrencyPickerDropdown(
            label = "A",
            selected = uiState.toCurrency,
            currencies = CurrencyCatalog.supported,
            onSelected = { onEvent(CurrencyConverterEvent.ToCurrencySelected(it)) },
            accentColor = grayConverter,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(DesignTokens.contentPaddingSmall))

        // Resultado
        when {
            uiState.isLoading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = grayConverter)
                }
            }
            uiState.errorMessage != null -> {
                SwissKitCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(DesignTokens.contentPaddingMedium)
                    )
                }
            }
            uiState.isResultAvailable -> {
                SwissKitCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(DesignTokens.contentPaddingMedium)) {
                        Text(
                            text = "Resultado",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.convertedResult,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
