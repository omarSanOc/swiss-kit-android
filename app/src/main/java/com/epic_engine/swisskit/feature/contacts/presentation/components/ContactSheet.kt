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
import androidx.compose.ui.res.stringResource
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
                text = if (isEditing) stringResource(R.string.contacts_sheet_edit_title) else stringResource(R.string.contacts_sheet_new_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(ContactsDesignTokens.spacingMediumPadding))
            ContactsTextField(
                value = nameDraft,
                onValueChange = onNameChange,
                placeholder = stringResource(R.string.contacts_sheet_name_placeholder),
                placeholderColor = ContactsDesignTokens.ContactsFieldPlaceholder,
                leadingIcon = R.drawable.icon_user,
                leadingIconDescription = stringResource(R.string.contacts_sheet_name_cd)
            )
            Spacer(Modifier.height(ContactsDesignTokens.spacingSmallPadding))
            ContactsTextField(
                value = phoneDraft,
                onValueChange = onPhoneChange,
                placeholder = stringResource(R.string.contacts_sheet_phone_placeholder),
                placeholderColor = ContactsDesignTokens.ContactsFieldPlaceholder,
                leadingIcon = R.drawable.icon_phone,
                leadingIconDescription = stringResource(R.string.contacts_sheet_phone_cd),
                isError = phoneError != null,
                supportingText = phoneError,
                keyboardType = KeyboardType.Phone
            )
            Spacer(Modifier.height(ContactsDesignTokens.spacingSmallPadding))
            ContactsPrimaryButton(
                text = stringResource(R.string.common_save),
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
