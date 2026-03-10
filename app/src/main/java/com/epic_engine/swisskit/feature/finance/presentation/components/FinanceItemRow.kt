package com.epic_engine.swisskit.feature.finance.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinanceItemRow(
    item: Finance,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("es"))
    val isIncome = item.type == FinanceType.INCOME
    val amountColor = if (isIncome) FinanceDesignTokens.incomeColor else FinanceDesignTokens.expenseColor

    SwissKitCard(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        containerColor = if (isSelected) FinanceDesignTokens.backgroundBlue else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = amountColor.copy(alpha = 0.15f), modifier = Modifier.size(FinanceDesignTokens.rowIconSize)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = if (isIncome) "Ingreso" else "Gasto",
                        tint = amountColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(
                    "${item.category} · ${dateFormat.format(Date(item.date))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.55f)
                )
            }
            Text(
                text = (if (isIncome) "+" else "-") + currencyFormat.format(item.amount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            if (isSelectionMode) {
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(checked = isSelected, onCheckedChange = { onClick() })
            }
        }
    }
}
