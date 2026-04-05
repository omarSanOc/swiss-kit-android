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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDesignTokens

@Composable
fun ContactsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: Int,
    leadingIconDescription: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    placeholderColor: Color = ContactsDesignTokens.ContactsFieldPlaceholder,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    val shape = RoundedCornerShape(ContactsDesignTokens.spacingSmallPadding)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = ContactsDesignTokens.spacingXXSmallPadding,
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
                painter = painterResource(leadingIcon),
                contentDescription = leadingIconDescription,
                tint = ContactsDesignTokens.Primary
            )
        },
        isError = isError,
        supportingText = supportingText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ContactsDesignTokens.Primary,
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
