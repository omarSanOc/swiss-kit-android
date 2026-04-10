package com.epic_engine.swisskit.feature.home.presentation.model

import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.feature.home.presentation.theme.HomeDesignTokens
import com.epic_engine.swisskit.navigation.SwissKitDestination

/** Static catalog of the 6 app modules shown on the Home screen. */
object ToolCatalog {

    val all: List<Tool> = listOf(
        Tool(
            id = "finance",
            name = R.string.home_finance_name,
            description = R.string.home_finance_description,
            icon = R.drawable.icon_wallet,
            color = HomeDesignTokens.blueFinance,
            backgroundColor = HomeDesignTokens.blueBackground,
            destination = SwissKitDestination.Finance
        ),
        Tool(
            id = "contacts",
            name = R.string.home_contacts_name,
            description = R.string.home_contacts_description,
            icon = R.drawable.icon_contact,
            color = HomeDesignTokens.greenContact,
            backgroundColor = HomeDesignTokens.greenBackground,
            destination = SwissKitDestination.Contacts
        ),
        Tool(
            id = "notes",
            name = R.string.home_notes_name,
            description = R.string.home_notes_description,
            icon = R.drawable.icon_notes,
            color = HomeDesignTokens.purpleNotes,
            backgroundColor = HomeDesignTokens.purpleBackground,
            destination = SwissKitDestination.Notes
        ),
        Tool(
            id = "qr_scanner",
            name = R.string.home_qr_name,
            description = R.string.home_qr_description,
            icon = R.drawable.icon_qr,
            color = HomeDesignTokens.pinkQrScanner,
            backgroundColor = HomeDesignTokens.pinkBackground,
            destination = SwissKitDestination.QrScanner
        ),
        Tool(
            id = "converter",
            name = R.string.home_converter_name,
            description = R.string.home_converter_description,
            icon = R.drawable.icon_converter,
            color = HomeDesignTokens.grayConverter,
            backgroundColor = HomeDesignTokens.grayBackground,
            destination = SwissKitDestination.Converter
        ),
        Tool(
            id = "shopping",
            name = R.string.home_shopping_name,
            description = R.string.home_shopping_description,
            icon = R.drawable.icon_shopping,
            color = HomeDesignTokens.yellowShopping,
            backgroundColor = HomeDesignTokens.yellowBackground,
            destination = SwissKitDestination.Shopping
        )
    )
}
