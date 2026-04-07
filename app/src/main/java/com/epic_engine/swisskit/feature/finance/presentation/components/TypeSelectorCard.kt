package com.epic_engine.swisskit.feature.finance.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.DesignTokens

import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.presentation.theme.FinanceDesignTokens

@Composable
fun TypeSelectorCard(
    type: FinanceType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpense = type == FinanceType.EXPENSE
    val activeColor = if (isExpense) FinanceDesignTokens.expenseColor else FinanceDesignTokens.incomeColor
    val activeBgColor = if (isExpense) FinanceDesignTokens.expenseColor.copy(alpha = FinanceDesignTokens.typeSelectionFillAlpha)
                        else FinanceDesignTokens.incomeColor.copy(alpha = FinanceDesignTokens.typeSelectionFillAlpha)
    val label = if (isExpense) stringResource(R.string.edit_finance_expense) else stringResource(R.string.edit_finance_income)
    val subtitle = if (isExpense) stringResource(R.string.edit_finance_expense_description) else stringResource(R.string.edit_finance_income_description)
    val icon = if (isExpense) R.drawable.icon_expense else R.drawable.icon_income

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else Color.Gray.copy(alpha = 0.35f),
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
        label = "border_color_${type.name}"
    )
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) activeBgColor else Color.White,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
        label = "bg_color_${type.name}"
    )
    val animatedLabelColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else Color.Black,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
        label = "label_color_${type.name}"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                onClickLabel = "Seleccionar $label"
            )
            .semantics { contentDescription = "$label: ${if (isSelected) "seleccionado" else "no seleccionado"}" },
        shape = RoundedCornerShape(DesignTokens.dimensXMedium),
        color = animatedBgColor,
        border = BorderStroke(DesignTokens.dimensXXXXSmall, animatedBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.dimensSmall),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.dimensXXSmall)
        ) {
            Surface(
                shape = CircleShape,
                color = activeColor.copy(alpha = 0.15f),
                modifier = Modifier.size(FinanceDesignTokens.dimensLarge)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = activeColor,
                        modifier = Modifier.size(DesignTokens.dimensXMedium)
                    )
                }
            }

            Text(
                text = label,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = animatedLabelColor
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(FinanceDesignTokens.dimensSmall))

            Box(
                modifier = Modifier
                    .size(FinanceDesignTokens.typeCardIndicator)
                    .border(DesignTokens.dimensXXXXSmall, animatedBorderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(FinanceDesignTokens.dimensSmall)
                            .background(activeColor, CircleShape)
                    )
                }
            }
        }
    }
}
