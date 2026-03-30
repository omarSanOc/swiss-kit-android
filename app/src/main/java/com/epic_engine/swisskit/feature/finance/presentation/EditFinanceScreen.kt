package com.epic_engine.swisskit.feature.finance.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.core.designsystem.components.SwissKitBackground
import com.epic_engine.swisskit.core.designsystem.components.SwissKitButton
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.core.designsystem.components.SwissKitTextField
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.presentation.components.TypeSelectorCard
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditFinanceScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditFinanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }
    var showNewCategoryInput by rememberSaveable { mutableStateOf(false) }
    var newCategoryText by rememberSaveable { mutableStateOf("") }

    val dateDisplayFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")) }

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            onNavigateBack()
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.date
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.onEvent(EditFinanceEvent.DateChanged(millis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    SwissKitBackground(colors = listOf(
        FinanceDesignTokens.primaryBlue,
        FinanceDesignTokens.backgroundLight,
    ),
        darkColors =  listOf(
            FinanceDesignTokens.primaryBlue,
            FinanceDesignTokens.backgroundDark
        ),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = if (uiState.isEditing) "Editar finanza" else "Nueva finanza",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = Color.Transparent
                            )
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = FinanceDesignTokens.listRowInset, vertical = 12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Tipo
                        SwissKitCard(contentPadding = PaddingValues(20.dp)) {
                            Text(
                                text = "Tipo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color =  MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                TypeSelectorCard(
                                    type = FinanceType.EXPENSE,
                                    isSelected = uiState.type == FinanceType.EXPENSE,
                                    onClick = { viewModel.onEvent(EditFinanceEvent.TypeChanged(FinanceType.EXPENSE)) },
                                    modifier = Modifier.weight(1f)
                                )
                                TypeSelectorCard(
                                    type = FinanceType.INCOME,
                                    isSelected = uiState.type == FinanceType.INCOME,
                                    onClick = { viewModel.onEvent(EditFinanceEvent.TypeChanged(FinanceType.INCOME)) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // 2. Monto
                        SwissKitCard(contentPadding = PaddingValues(20.dp)) {
                            Text(
                                text = "Monto *",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color =  MaterialTheme.colorScheme.onSurface
                            )
                            SwissKitTextField(
                                value = uiState.amountInput,
                                onValueChange = { viewModel.onEvent(EditFinanceEvent.AmountChanged(it)) },
                                label = "",
                                placeholder = "0",
                                leadingIcon = Icons.Default.AttachMoney,
                                accentColor = if (uiState.type == FinanceType.EXPENSE) FinanceDesignTokens.expenseRed
                                else FinanceDesignTokens.incomeGreen,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // 3. Título
                        SwissKitCard(contentPadding = PaddingValues(20.dp)) {
                            Text(
                                text = "Título *",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color =  MaterialTheme.colorScheme.onSurface
                            )
                            SwissKitTextField(
                                value = uiState.title,
                                onValueChange = { viewModel.onEvent(EditFinanceEvent.TitleChanged(it)) },
                                label = "",
                                placeholder = "Escribe tu título",
                                accentColor = FinanceDesignTokens.primaryBlue,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        // 4. Fecha
                        SwissKitCard(contentPadding = PaddingValues(20.dp)) {
                            Text(
                                text = "Fecha",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color =  MaterialTheme.colorScheme.onSurface
                            )
                            AssistChip(
                                onClick = { showDatePicker = true },
                                label = {
                                    Text(
                                        text = dateDisplayFormat.format(Date(uiState.date)),
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Seleccionar fecha",
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                shape = RoundedCornerShape(FinanceDesignTokens.chipRadius),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = FinanceDesignTokens.primaryBlue.copy(alpha = 0.12f),
                                    labelColor = FinanceDesignTokens.primaryBlue,
                                    leadingIconContentColor = FinanceDesignTokens.primaryBlue
                                ),
                                border = null
                            )
                        }

                        // 5. Categoría
                        SwissKitCard(contentPadding = PaddingValues(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Categoría",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(
                                    onClick = { showNewCategoryInput = !showNewCategoryInput },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Agregar categoría",
                                        tint = FinanceDesignTokens.primaryBlue
                                    )
                                }
                            }

                            if (showNewCategoryInput) {
                                SwissKitTextField(
                                    value = newCategoryText,
                                    onValueChange = { text ->
                                        newCategoryText = text
                                        val trimmed = text.trim()
                                        if (trimmed.isNotBlank()) {
                                            viewModel.onEvent(EditFinanceEvent.CategoryChanged(trimmed))
                                        }
                                    },
                                    label = "",
                                    placeholder = "Nueva categoría",
                                    accentColor = FinanceDesignTokens.primaryBlue,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.availableCategories.forEach { cat ->
                                    FilterChip(
                                        selected = uiState.category == cat,
                                        onClick = {
                                            viewModel.onEvent(EditFinanceEvent.CategoryChanged(cat))
                                            newCategoryText = cat
                                        },
                                        label = { Text(cat) },
                                        shape = RoundedCornerShape(FinanceDesignTokens.chipRadius),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = FinanceDesignTokens.primaryBlue,
                                            selectedLabelColor = Color.White,
                                            containerColor = Color.Black.copy(alpha = 0.06f),
                                            labelColor = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = uiState.category == cat,
                                            borderWidth = 0.dp,
                                            selectedBorderWidth = 0.dp
                                        )
                                    )
                                }
                            }
                        }

                        // 6. Notas
                        SwissKitCard(contentPadding = PaddingValues(20.dp)) {
                            Text(
                                text = "Notas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color =  MaterialTheme.colorScheme.onSurface
                                )
                            SwissKitTextField(
                                value = uiState.notes,
                                onValueChange = { viewModel.onEvent(EditFinanceEvent.NotesChanged(it)) },
                                label = "",
                                placeholder = "Escribe tus notas",
                                accentColor = FinanceDesignTokens.primaryBlue,
                                maxLines = 4,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(FinanceDesignTokens.notesMinHeight)
                            )
                        }

                        // Validation error
                        uiState.validationError?.let { error ->
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        // 7. Save button (outside any card)
                        SwissKitButton(
                            text = "Guardar",
                            onClick = { viewModel.onEvent(EditFinanceEvent.Save) },
                            containerColor = FinanceDesignTokens.primaryBlue,
                            enabled = uiState.canSave && !uiState.isSaving,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    )
}
