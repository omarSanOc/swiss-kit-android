package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDimens

@Composable
fun ContactsCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(ContactsDimens.cardCornerRadius)
    Surface(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
        shadowElevation = ContactsDimens.cardElevation,
        tonalElevation = ContactsDimens.cardElevation
    ) {
        Box(modifier = Modifier.padding(ContactsDimens.cardInternalPadding)) {
            content()
        }
    }
}
