package com.epic_engine.swisskit.feature.converter.presentation.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitToast
import com.epic_engine.swisskit.feature.converter.domain.model.CurrencyCatalog
import com.epic_engine.swisskit.feature.converter.presentation.theme.ConverterDesignTokens
import com.epic_engine.swisskit.feature.converter.presentation.utils.CurrencyConverterEvent
import com.epic_engine.swisskit.feature.converter.presentation.utils.CurrencyConverterUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterContent(
    uiState: CurrencyConverterUiState,
    onEvent: (CurrencyConverterEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { onEvent(CurrencyConverterEvent.Refresh) },
        state = pullToRefreshState,
        modifier = modifier.fillMaxSize(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
                isRefreshing = uiState.isRefreshing,
                color = ConverterDesignTokens.accentBlue
            )
        }

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DesignTokens.dimensMedium)
                .padding(bottom = DesignTokens.dimensXXXMedium),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.dimensXXXMedium)
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
                    Spacer(modifier = Modifier.height(DesignTokens.dimensXSmall))
                    Text(
                        text = "Misma moneda seleccionada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Loading (solo carga inicial sin datos previos)
                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(DesignTokens.dimensXSmall))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = ConverterDesignTokens.accentBlue
                    )
                }

                // Error
                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(DesignTokens.dimensXSmall))
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Offline indicator
                if (uiState.isOffline) {
                    Spacer(modifier = Modifier.height(DesignTokens.dimensXXSmall))
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
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(
                                        ClipData.newPlainText("resultado_conversion", uiState.convertedResult)
                                    )
                                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                        onEvent(CurrencyConverterEvent.ShowCopiedToast)
                                    }
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

        // Toast de confirmación (solo en Android 12L o menor)
        SwissKitToast(
            message = if (uiState.showCopiedToast) "Copiado al portapapeles" else null,
            onDismiss = { onEvent(CurrencyConverterEvent.DismissCopiedToast) }
        )
    }
}
