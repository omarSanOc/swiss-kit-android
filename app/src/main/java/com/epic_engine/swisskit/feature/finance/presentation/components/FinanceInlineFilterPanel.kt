package com.epic_engine.swisskit.feature.finance.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.feature.finance.domain.usecase.FinanceCategorySelectionEngine
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinanceInlineFilterPanel(
    visible: Boolean,
    availableCategories: List<String> = emptyList(),
    selectedCategories: Set<String> = emptySet(),
    onToggleCategoryFilter: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val engine = remember { FinanceCategorySelectionEngine() }

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        SwissKitCard(contentPadding = PaddingValues(8.dp)) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val allChips = listOf(
                    FinanceCategorySelectionEngine.LABEL_ALL,
                    FinanceCategorySelectionEngine.LABEL_INCOME,
                    FinanceCategorySelectionEngine.LABEL_EXPENSE
                ) + availableCategories

                allChips.forEach { label ->
                    val isSelected = engine.isSelected(label, selectedCategories)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onToggleCategoryFilter(label) },
                        label = {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color.DarkGray
                            )
                        },
                        shape = RoundedCornerShape(FinanceDesignTokens.chipRadius),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FinanceDesignTokens.primaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFE0E0E0),
                            labelColor = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderWidth = 0.dp,
                            selectedBorderWidth = 0.dp
                        )
                    )
                }
            }
        }
    }
}
