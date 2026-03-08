package com.epic_engine.swisskit.feature.converter.presentation.unit

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
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.core.designsystem.components.SwissKitTextField
import com.epic_engine.swisskit.feature.converter.domain.model.UnitCategory
import com.epic_engine.swisskit.feature.converter.presentation.components.UnitPickerDropdown
import com.epic_engine.swisskit.ui.theme.grayConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterContent(
    uiState: UnitConverterUiState,
    onEvent: (UnitConverterEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(DesignTokens.contentPaddingMedium),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.contentPaddingMedium)
    ) {
        // Selector de categoría: Longitud / Peso / Volumen
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            UnitCategory.all.forEachIndexed { index, category ->
                SegmentedButton(
                    selected = uiState.selectedCategory == category,
                    onClick = { onEvent(UnitConverterEvent.CategorySelected(category)) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = UnitCategory.all.size
                    ),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = grayConverter,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = category.displayName)
                }
            }
        }

        // Monto
        SwissKitTextField(
            value = uiState.amountInput,
            onValueChange = { onEvent(UnitConverterEvent.AmountChanged(it)) },
            label = "Valor",
            accentColor = grayConverter,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // Unidad origen
        UnitPickerDropdown(
            label = "De",
            selected = uiState.fromUnit,
            units = uiState.availableUnits,
            onSelected = { onEvent(UnitConverterEvent.FromUnitSelected(it)) },
            accentColor = grayConverter,
            modifier = Modifier.fillMaxWidth()
        )

        // Botón swap
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { onEvent(UnitConverterEvent.SwapUnits) }) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Intercambiar unidades",
                    tint = grayConverter
                )
            }
        }

        // Unidad destino
        UnitPickerDropdown(
            label = "A",
            selected = uiState.toUnit,
            units = uiState.availableUnits,
            onSelected = { onEvent(UnitConverterEvent.ToUnitSelected(it)) },
            accentColor = grayConverter,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(DesignTokens.contentPaddingSmall))

        // Resultado
        if (uiState.convertedResult.isNotBlank()) {
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
