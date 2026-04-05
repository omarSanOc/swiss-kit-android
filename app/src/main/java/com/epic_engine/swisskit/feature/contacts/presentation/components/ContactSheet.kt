package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDesignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSheet(
    nameDraft: String,
    phoneDraft: String,
    phoneError: String?,
    isEditing: Boolean,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onConfirm: () -> Unit,
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
                .padding(horizontal = ContactsDesignTokens.spacingXXMediumPadding)
                .navigationBarsPadding()
                .padding(bottom = ContactsDesignTokens.spacingMediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(ContactsDesignTokens.spacingXSmallPadding))
            Text(
                text = if (isEditing) "Editar contacto" else "Nuevo contacto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(ContactsDesignTokens.spacingMediumPadding))
            ContactsTextField(
                value = nameDraft,
                onValueChange = onNameChange,
                placeholder = "Nombre",
                placeholderColor = ContactsDesignTokens.ContactsFieldPlaceholder,
                leadingIcon = R.drawable.icon_user,
                leadingIconDescription = "Persona"
            )
            Spacer(Modifier.height(ContactsDesignTokens.spacingSmallPadding))
            ContactsTextField(
                value = phoneDraft,
                onValueChange = onPhoneChange,
                placeholder = "Teléfono",
                placeholderColor = ContactsDesignTokens.ContactsFieldPlaceholder,
                leadingIcon = R.drawable.icon_phone,
                leadingIconDescription = "Teléfono",
                isError = phoneError != null,
                supportingText = phoneError,
                keyboardType = KeyboardType.Phone
            )
            Spacer(Modifier.height(ContactsDesignTokens.spacingSmallPadding))
            ContactsPrimaryButton(
                text = "Guardar",
                onClick = onConfirm,
                enabled = nameDraft.isNotBlank() && phoneDraft.isNotBlank()
            )
            ContactsCancelButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
