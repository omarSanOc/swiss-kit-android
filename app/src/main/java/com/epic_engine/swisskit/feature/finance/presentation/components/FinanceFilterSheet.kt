package com.epic_engine.swisskit.feature.finance.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceCategoryData
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.presentation.FinanceEvent
import com.epic_engine.swisskit.feature.finance.presentation.FinanceUiState
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FinanceFilterSheet(
    uiState: FinanceUiState,
    onEvent: (FinanceEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filtros", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { onEvent(FinanceEvent.ClearFilters); onDismiss() }) {
                    Text("Limpiar", color = FinanceDesignTokens.accentBlue)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("Tipo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(null to "Todos", FinanceType.INCOME to "Ingresos", FinanceType.EXPENSE to "Gastos").forEach { (type, label) ->
                    FilterChip(
                        selected = uiState.typeFilter == type,
                        onClick = { onEvent(FinanceEvent.SetTypeFilter(type)) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FinanceDesignTokens.accentBlue,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("Categorías", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            Spacer(modifier = Modifier.height(8.dp))
            val allCategories = FinanceCategoryData.incomeCategories + FinanceCategoryData.expenseCategories
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                allCategories.forEach { cat ->
                    FilterChip(
                        selected = cat in uiState.selectedCategories,
                        onClick = { onEvent(FinanceEvent.ToggleCategoryFilter(cat)) },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FinanceDesignTokens.accentBlue,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
