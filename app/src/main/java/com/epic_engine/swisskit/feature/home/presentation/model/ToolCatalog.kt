package com.epic_engine.swisskit.feature.home.presentation.model

import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.feature.home.presentation.theme.HomeDesignTokens
import com.epic_engine.swisskit.navigation.SwissKitDestination

/**
 * Equivalente al enum ToolRoute de iOS + lista de herramientas de HomeView.
 * Catálogo estático de los 6 módulos de la app.
 */
object ToolCatalog {

    val all: List<Tool> = listOf(
        Tool(
            id = "finance",
            name = "Finanzas",
            description = "Maneja finanzas",
            icon = R.drawable.icon_wallet,
            color = HomeDesignTokens.blueFinance,
            backgroundColor = HomeDesignTokens.blueBackground,
            destination = SwissKitDestination.Finance
        ),
        Tool(
            id = "contacts",
            name = "Contactos",
            description = "Gestiona contactos",
            icon = R.drawable.icon_contact,
            color = HomeDesignTokens.greenContact,
            backgroundColor = HomeDesignTokens.greenBackground,
            destination = SwissKitDestination.Contacts
        ),
        Tool(
            id = "notes",
            name = "Notas",
            description = "Escribe tus notas",
            icon = R.drawable.icon_notes,
            color = HomeDesignTokens.purpleNotes,
            backgroundColor = HomeDesignTokens.purpleBackground,
            destination = SwissKitDestination.Notes
        ),
        Tool(
            id = "qr_scanner",
            name = "Código QR",
            description = "Escanea / Genera",
            icon = R.drawable.icon_qr,
            color = HomeDesignTokens.pinkQrScanner,
            backgroundColor = HomeDesignTokens.pinkBackground,
            destination = SwissKitDestination.QrScanner
        ),
        Tool(
            id = "converter",
            name = "Conversor",
            description = "Unidades / Divisas",
            icon = R.drawable.icon_converter,
            color = HomeDesignTokens.grayConverter,
            backgroundColor = HomeDesignTokens.grayBackground,
            destination = SwissKitDestination.Converter
        ),
        Tool(
            id = "shopping",
            name = "Compras",
            description = "Lista de compras",
            icon = R.drawable.icon_shopping,
            color = HomeDesignTokens.yellowShopping,
            backgroundColor = HomeDesignTokens.yellowBackground,
            destination = SwissKitDestination.Shopping
        )
    )
}
