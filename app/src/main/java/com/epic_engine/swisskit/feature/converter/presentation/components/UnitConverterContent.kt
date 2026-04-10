package com.epic_engine.swisskit.feature.converter.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.feature.converter.domain.model.UnitCategory
import com.epic_engine.swisskit.feature.converter.presentation.theme.ConverterDesignTokens
import com.epic_engine.swisskit.feature.converter.presentation.utils.UnitConverterEvent
import com.epic_engine.swisskit.feature.converter.presentation.utils.UnitConverterUiState

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
            .padding(horizontal = DesignTokens.dimensMedium)
            .padding(bottom = DesignTokens.dimensXXXMedium),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.dimensXXXMedium)
    ) {
        // Card: Category
        ConverterSectionCard(title = stringResource(R.string.converter_category_title)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.dimensSmall)
            ) {
                CategoryDropdown(
                    selected = uiState.selectedCategory,
                    onSelected = { onEvent(UnitConverterEvent.CategorySelected(it)) },
                    modifier = Modifier.weight(1f)
                )
                AnimatedContent(
                    targetState = uiState.selectedCategory,
                    transitionSpec = {
                        (scaleIn(initialScale = 0.8f) + fadeIn()) togetherWith
                            (scaleOut(targetScale = 0.8f) + fadeOut())
                    },
                    label = "category_icon"
                ) { category ->
                    Icon(
                        painter = painterResource(id = categoryIconRes(category)),
                        contentDescription = category.displayName,
                        tint = ConverterDesignTokens.accentBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Card: Units
        ConverterSectionCard(title = stringResource(R.string.converter_unit_title)) {
            UnitPickerDropdown(
                label = "De",
                selected = uiState.fromUnit,
                units = uiState.availableUnits,
                onSelected = { onEvent(UnitConverterEvent.FromUnitSelected(it)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(DesignTokens.dimensSmall))
            UnitPickerDropdown(
                label = "A",
                selected = uiState.toUnit,
                units = uiState.availableUnits,
                onSelected = { onEvent(UnitConverterEvent.ToUnitSelected(it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Card: Amount
        ConverterSectionCard(title = stringResource(R.string.converter_quantity_title)) {
            ConverterOutlinedField(
                value = uiState.amountInput,
                onValueChange = { onEvent(UnitConverterEvent.AmountChanged(it)) },
                placeholder = "0",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Card: Result
        ConverterSectionCard(title = stringResource(R.string.converter_result_title)) {
            ConverterReadOnlyField(
                value = uiState.convertedResult,
                placeholder = "—",
                modifier = Modifier.fillMaxWidth(),
                trailingContent = if (uiState.convertedResult.isNotBlank()) {
                    {
                        Text(
                            text = uiState.toUnit.symbol,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = DesignTokens.dimensXXXSmall)
                        )
                    }
                } else null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selected: UnitCategory,
    onSelected: (UnitCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        ConverterSelectorField(
            value = selected.displayName,
            placeholder = "",
            expanded = expanded,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            UnitCategory.all.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.displayName) },
                    onClick = {
                        onSelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun categoryIconRes(category: UnitCategory): Int = when (category) {
    UnitCategory.Length -> R.drawable.icon_ruler
    UnitCategory.Weight -> R.drawable.icon_weight
    UnitCategory.Volume -> R.drawable.icon_volume
}
