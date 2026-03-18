package com.epic_engine.swisskit.feature.contacts.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsDimens
import com.epic_engine.swisskit.feature.contacts.presentation.theme.ContactsTeal

enum class ContactsIconButtonVariant { Filled, Outlined }

@Composable
fun ContactsIconButton(
    icon: Int,
    iconDescription: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ContactsIconButtonVariant = ContactsIconButtonVariant.Filled
) {
    val shape = RoundedCornerShape(ContactsDimens.iconButtonCornerRadius)
    val contentPadding = androidx.compose.foundation.layout.PaddingValues(
        vertical = ContactsDimens.iconButtonVerticalPadding,
        horizontal = ContactsDimens.iconButtonHorizontalPadding
    )

    when (variant) {
        ContactsIconButtonVariant.Filled -> Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .shadow(elevation = ContactsDimens.iconButtonElevation, shape = shape),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = ContactsTeal,
                contentColor = Color.White
            ),
            contentPadding = contentPadding
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = iconDescription,
                    modifier = Modifier.size(ContactsDimens.iconButtonIconSize)
                )
                Spacer(Modifier.width(ContactsDimens.iconTextGap))
                Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        ContactsIconButtonVariant.Outlined -> OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .shadow(elevation = ContactsDimens.iconButtonElevation, shape = shape),
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = ContactsTeal
            ),
            contentPadding = contentPadding
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = iconDescription,
                    tint = ContactsTeal,
                    modifier = Modifier.size(ContactsDimens.iconButtonIconSize)
                )
                Spacer(Modifier.width(ContactsDimens.iconTextGap))
                Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = ContactsTeal)
            }
        }
    }
}
