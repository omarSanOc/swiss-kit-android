package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.epic_engine.swisskit.R.drawable
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDimens
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTeal

@Composable
fun ContactsCancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Cancelar"
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = ContactsDimens.cancelButtonMinHeight)
    ) {
        Icon(
            painter = painterResource(drawable.icon_close),
            contentDescription = "Cerrar",
            tint = ContactsTeal
        )
        Text(
            text = label,
            color = ContactsTeal
        )
    }
}
