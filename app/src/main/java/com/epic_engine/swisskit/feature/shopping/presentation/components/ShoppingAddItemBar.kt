package com.epic_engine.swisskit.feature.shopping.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.core.designsystem.components.SwissKitButton
import com.epic_engine.swisskit.core.designsystem.components.SwissKitTextField
import com.epic_engine.swisskit.ui.theme.yellowShopping

@Composable
fun ShoppingAddItemBar(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SwissKitTextField(
            value = value,
            onValueChange = onValueChange,
            label = "Nuevo ítem",
            leadingIcon = Icons.Default.ShoppingCart,
            accentColor = yellowShopping,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onAdd() }
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        SwissKitButton(
            text = "Agregar",
            onClick = onAdd,
            containerColor = yellowShopping,
            enabled = value.isNotBlank()
        )
    }
}
