package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsFieldPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactSheet(
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
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = if (isEditing) "Editar contacto" else "Nuevo contacto",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = nameDraft,
                onValueChange = onNameChange,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = ContactsFieldPlaceholder,
                    unfocusedLabelColor = ContactsFieldPlaceholder,
                    disabledLabelColor = ContactsFieldPlaceholder,
                    errorLabelColor = ContactsFieldPlaceholder,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = phoneDraft,
                onValueChange = onPhoneChange,
                label = { Text("Teléfono") },
                singleLine = true,
                isError = phoneError != null,
                supportingText = phoneError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = ContactsFieldPlaceholder,
                    unfocusedLabelColor = ContactsFieldPlaceholder,
                    disabledLabelColor = ContactsFieldPlaceholder,
                    errorLabelColor = ContactsFieldPlaceholder,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    errorTextColor = Color.Black
                )
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
                TextButton(
                    onClick = onConfirm,
                    enabled = nameDraft.isNotBlank() && phoneDraft.isNotBlank()
                ) {
                    Text("Guardar")
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
