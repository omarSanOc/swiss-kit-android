package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
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
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDimens
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsFieldPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySheet(
    title: String,
    onTitleChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isRenaming: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContactsDimens.formSectionSpacing + 4.dp)
                .navigationBarsPadding()
                .padding(bottom = ContactsDimens.formSectionSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (isRenaming) "Renombrar categoría" else "Nueva categoría",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(ContactsDimens.formSectionSpacing))
            ContactsTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = "Nombre de la categoría",
                placeholderColor = ContactsFieldPlaceholder,
                leadingIcon = R.drawable.icon_folder,
                leadingIconDescription = "Carpeta"
            )
            Spacer(Modifier.height(ContactsDimens.formFieldSpacing))
            ContactsPrimaryButton(
                text = "Guardar",
                onClick = onConfirm,
                enabled = title.isNotBlank()
            )
            ContactsCancelButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
