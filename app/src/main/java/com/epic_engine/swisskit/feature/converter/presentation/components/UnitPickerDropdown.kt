package com.epic_engine.swisskit.feature.converter.presentation.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.epic_engine.swisskit.feature.converter.domain.model.MeasurementUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitPickerDropdown(
    label: String,
    selected: MeasurementUnit?,
    units: List<MeasurementUnit>,
    onSelected: (MeasurementUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        ConverterSelectorField(
            value = if (selected != null) "${stringResource(selected.nameRes)}  (${selected.symbol})" else "",
            placeholder = label,
            expanded = expanded,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text("${stringResource(unit.nameRes)}  (${unit.symbol})") },
                    onClick = {
                        onSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}
