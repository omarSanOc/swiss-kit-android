package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.feature.contacts.domain.model.Contact
import com.epic_engine.swisskit.feature.contacts.domain.model.ContactAction
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactActionSheet(
    contact: Contact,
    onAction: (ContactAction) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContactsDimens.screenHorizontalPadding)
                .navigationBarsPadding()
                .padding(bottom = ContactsDimens.formSectionSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(36.dp))
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(18.dp))
            ContactsIconButton(
                icon = R.drawable.icon_phone,
                iconDescription = "Llamar",
                label = "Llamar",
                onClick = { onAction(ContactAction.CALL) },
                variant = ContactsIconButtonVariant.Filled
            )
            Spacer(Modifier.height(ContactsDimens.formFieldSpacing))
            ContactsIconButton(
                icon = R.drawable.icon_whatsapp,
                iconDescription = "WhatsApp",
                label = "WhatsApp",
                onClick = { onAction(ContactAction.WHATSAPP) },
                variant = ContactsIconButtonVariant.Outlined
            )
            ContactsCancelButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
