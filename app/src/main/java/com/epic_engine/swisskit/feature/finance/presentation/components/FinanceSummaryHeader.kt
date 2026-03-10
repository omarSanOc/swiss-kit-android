package com.epic_engine.swisskit.feature.finance.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.components.SwissKitCard
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FinanceSummaryHeader(
    totalIncome: Double,
    totalExpenses: Double,
    netBalance: Double,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    SwissKitCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryColumn("Ingresos", currencyFormat.format(totalIncome), FinanceDesignTokens.incomeColor)
            SummaryColumn("Gastos", currencyFormat.format(totalExpenses), FinanceDesignTokens.expenseColor)
            SummaryColumn(
                label = "Neto",
                value = currencyFormat.format(netBalance),
                color = if (netBalance >= 0) FinanceDesignTokens.incomeColor else FinanceDesignTokens.expenseColor
            )
        }
    }
}

@Composable
private fun SummaryColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}
