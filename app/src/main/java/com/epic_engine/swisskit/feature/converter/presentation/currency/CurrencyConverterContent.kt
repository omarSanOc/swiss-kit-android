package com.epic_engine.swisskit.feature.converter.presentation.currency

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.components.SwissKitToast
import com.epic_engine.swisskit.feature.converter.domain.model.CurrencyCatalog
import com.epic_engine.swisskit.feature.converter.presentation.components.ConverterOutlinedField
import com.epic_engine.swisskit.feature.converter.presentation.components.ConverterReadOnlyField
import com.epic_engine.swisskit.feature.converter.presentation.components.ConverterSectionCard
import com.epic_engine.swisskit.feature.converter.presentation.components.CurrencyPickerDropdown
import com.epic_engine.swisskit.feature.converter.presentation.theme.ConverterDesignTokens

@Composable
fun CurrencyConverterContent(
    uiState: CurrencyConverterUiState,
    onEvent: (CurrencyConverterEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ConverterDesignTokens.screenHorizontalPadding)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(ConverterDesignTokens.sectionSpacing)
        ) {
            // Card: Divisas
            ConverterSectionCard(title = "Divisas") {
                // Picker "De"
                CurrencyPickerDropdown(
                    label = "De",
                    selected = uiState.fromCurrency,
                    currencies = CurrencyCatalog.supported,
                    onSelected = { onEvent(CurrencyConverterEvent.FromCurrencySelected(it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Swap button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = { onEvent(CurrencyConverterEvent.SwapCurrencies) }) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Intercambiar monedas",
                            tint = ConverterDesignTokens.accentBlue
                        )
                    }
                }

                // Picker "A"
                CurrencyPickerDropdown(
                    label = "A",
                    selected = uiState.toCurrency,
                    currencies = CurrencyCatalog.supported,
                    onSelected = { onEvent(CurrencyConverterEvent.ToCurrencySelected(it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Warning: misma moneda
                if (uiState.fromCurrency != null && uiState.toCurrency != null &&
                    uiState.fromCurrency == uiState.toCurrency
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Misma moneda seleccionada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Loading
                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = ConverterDesignTokens.accentBlue
                    )
                }

                // Error
                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Offline indicator
                if (uiState.isOffline) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Usando datos en caché",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Card: Monto
            ConverterSectionCard(title = "Monto") {
                ConverterOutlinedField(
                    value = uiState.amountInput,
                    onValueChange = { onEvent(CurrencyConverterEvent.AmountChanged(it)) },
                    placeholder = "0.00",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Card: Resultado
            ConverterSectionCard(title = "Resultado") {
                ConverterReadOnlyField(
                    value = uiState.convertedResult,
                    placeholder = "—",
                    modifier = Modifier.fillMaxWidth(),
                    trailingContent = if (uiState.isResultAvailable) {
                        {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(uiState.convertedResult))
                                    onEvent(CurrencyConverterEvent.ShowCopiedToast)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copiar resultado",
                                    tint = ConverterDesignTokens.accentBlue
                                )
                            }
                        }
                    } else null
                )
            }
        }

        // Toast de confirmación
        SwissKitToast(
            message = if (uiState.showCopiedToast) "Copiado al portapapeles" else null,
            onDismiss = { onEvent(CurrencyConverterEvent.DismissCopiedToast) }
        )
    }
}
