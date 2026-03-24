package com.epic_engine.swisskit.feature.finance.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens

@Composable
fun FinanceInlineFilterPanel(
    visible: Boolean,
    typeFilter: FinanceType?,
    onSetTypeFilter: (FinanceType?) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        SwissKitCard(contentPadding = PaddingValues(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    null to "Todas",
                    FinanceType.INCOME to "Ingreso",
                    FinanceType.EXPENSE to "Gasto"
                ).forEach { (type, label) ->
                    val isSelected = typeFilter == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSetTypeFilter(type) },
                        label = {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White
                                        else Color.DarkGray
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
