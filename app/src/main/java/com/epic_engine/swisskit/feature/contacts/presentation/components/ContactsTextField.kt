package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDimens
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsFieldPlaceholder
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTeal

@Composable
fun ContactsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    leadingIconDescription: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    placeholderColor: Color = ContactsFieldPlaceholder,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    val shape = RoundedCornerShape(ContactsDimens.textFieldCornerRadius)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = ContactsDimens.textFieldElevation,
                shape = shape
            ),
        shape = shape,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = placeholderColor
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = leadingIconDescription,
                tint = ContactsTeal
            )
        },
        isError = isError,
        supportingText = supportingText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ContactsTeal,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            errorTextColor = Color.Black
        ),
        visualTransformation = VisualTransformation.None
    )
}
