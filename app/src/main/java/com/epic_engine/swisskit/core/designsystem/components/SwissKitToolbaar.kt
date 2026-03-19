package com.epic_engine.swisskit.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDimens

@Composable
fun SwissKitToolbar(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ContactsDimens.screenHorizontalPadding)
            .height(36.dp),
    ) {
        Spacer(Modifier.height(ContactsDimens.screenTopPadding))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Spacer(Modifier.height(ContactsDimens.screenTopPadding))
    }
}