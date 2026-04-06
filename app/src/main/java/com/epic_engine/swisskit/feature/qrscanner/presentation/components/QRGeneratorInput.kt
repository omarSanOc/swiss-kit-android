package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.feature.qrscanner.presentation.theme.QRScannerDesignTokens

@Composable
fun QRGeneratorInput(
    value: String,
    onValueChange: (String) -> Unit,
    onGo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) QRScannerDesignTokens.InputBackgroundDark
    else QRScannerDesignTokens.InputBackground
    val textColor = MaterialTheme.colorScheme.onSurface
    val placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = DesignTokens.dimensXXLarge),
        shape = RoundedCornerShape(DesignTokens.dimensMedium),
        color = containerColor,
        tonalElevation = 0.dp
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
            cursorBrush = SolidColor(QRScannerDesignTokens.Primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = { onGo() }),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignTokens.dimensMedium, vertical = QRScannerDesignTokens.dimensMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_qr),
                        contentDescription = null,
                        tint = QRScannerDesignTokens.Primary,
                        modifier = Modifier.size(DesignTokens.dimensXXXMedium)
                    )
                    Spacer(Modifier.width(DesignTokens.dimensSmall))
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = "Ingresa texto o URL",
                                style = MaterialTheme.typography.bodyLarge,
                                color = placeholderColor
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}