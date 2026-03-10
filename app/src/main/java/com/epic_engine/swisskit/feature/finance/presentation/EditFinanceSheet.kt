package com.epic_engine.swisskit.feature.finance.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.core.designsystem.components.SwissKitButton
import com.epic_engine.swisskit.core.designsystem.components.SwissKitTextField
import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditFinanceSheet(
    existingItem: Finance? = null,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EditFinanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(existingItem) {
        existingItem?.let { viewModel.loadForEdit(it) }
    }

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) { onSaved(); onDismiss() }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (uiState.isEditing) "Editar transacción" else "Nueva transacción",
                style = MaterialTheme.typography.titleMedium
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                listOf(FinanceType.INCOME to "Ingreso", FinanceType.EXPENSE to "Gasto").forEachIndexed { idx, (type, label) ->
                    SegmentedButton(
                        selected = uiState.type == type,
                        onClick = { viewModel.onEvent(EditFinanceEvent.TypeChanged(type)) },
                        shape = SegmentedButtonDefaults.itemShape(index = idx, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = if (type == FinanceType.INCOME) FinanceDesignTokens.incomeColor else FinanceDesignTokens.expenseColor,
                            activeContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) { Text(label) }
                }
            }

            SwissKitTextField(
                value = uiState.title,
                onValueChange = { viewModel.onEvent(EditFinanceEvent.TitleChanged(it)) },
                label = "Título *",
                accentColor = FinanceDesignTokens.accentBlue,
                modifier = Modifier.fillMaxWidth()
            )

            SwissKitTextField(
                value = uiState.amountInput,
                onValueChange = { viewModel.onEvent(EditFinanceEvent.AmountChanged(it)) },
                label = "Monto *",
                accentColor = FinanceDesignTokens.accentBlue,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Categoría *", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.availableCategories.forEach { cat ->
                    FilterChip(
                        selected = uiState.category == cat,
                        onClick = { viewModel.onEvent(EditFinanceEvent.CategoryChanged(cat)) },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FinanceDesignTokens.accentBlue,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            SwissKitTextField(
                value = uiState.notes,
                onValueChange = { viewModel.onEvent(EditFinanceEvent.NotesChanged(it)) },
                label = "Notas (opcional)",
                accentColor = FinanceDesignTokens.accentBlue,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            uiState.validationError?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }

            SwissKitButton(
                text = if (uiState.isEditing) "Actualizar" else "Guardar",
                onClick = { viewModel.onEvent(EditFinanceEvent.Save) },
                containerColor = FinanceDesignTokens.accentBlue,
                enabled = uiState.canSave && !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
