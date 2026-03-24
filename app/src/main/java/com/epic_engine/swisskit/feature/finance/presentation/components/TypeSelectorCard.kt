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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.epic_engine.swisskit.R

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
    val activeColor = if (isExpense) FinanceDesignTokens.expenseRed else FinanceDesignTokens.incomeGreen
    val activeBgColor = if (isExpense) FinanceDesignTokens.expenseRed.copy(alpha = FinanceDesignTokens.typeSelectionFillAlpha)
                        else FinanceDesignTokens.incomeGreen.copy(alpha = FinanceDesignTokens.typeSelectionFillAlpha)
    val label = if (isExpense) "Gasto" else "Ingreso"
    val subtitle = if (isExpense) "Dinero que sale" else "Dinero que entra"
    val icon = if (isExpense) R.drawable.icon_expense else R.drawable.icon_income

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else Color.Gray.copy(alpha = 0.35f),
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
        label = "border_color_${type.name}"
    )
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) activeBgColor else FinanceDesignTokens.cardSurface,
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
        shape = RoundedCornerShape(FinanceDesignTokens.typeCardRadius),
        color = animatedBgColor,
        border = BorderStroke(FinanceDesignTokens.typeCardBorderWidth, animatedBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = activeColor.copy(alpha = 0.15f),
                modifier = Modifier.size(FinanceDesignTokens.typeCardIconSize + 12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = activeColor,
                        modifier = Modifier.size(FinanceDesignTokens.typeCardIconSize)
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

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(FinanceDesignTokens.typeCardIndicator)
                    .border(2.dp, animatedBorderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(activeColor, CircleShape)
                    )
                }
            }
        }
    }
}
