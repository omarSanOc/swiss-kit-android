package com.epic_engine.swisskit.feature.home.presentation.model

import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.navigation.SwissKitDestination
import com.epic_engine.swisskit.ui.theme.blueBackground
import com.epic_engine.swisskit.ui.theme.blueFinance
import com.epic_engine.swisskit.ui.theme.grayBackground
import com.epic_engine.swisskit.ui.theme.grayConverter
import com.epic_engine.swisskit.ui.theme.greenBackground
import com.epic_engine.swisskit.ui.theme.greenContact
import com.epic_engine.swisskit.ui.theme.orangeQrScanner
import com.epic_engine.swisskit.ui.theme.pinkBackground
import com.epic_engine.swisskit.ui.theme.pinkQRScanner
import com.epic_engine.swisskit.ui.theme.purpleBackground
import com.epic_engine.swisskit.ui.theme.purpleNotes
import com.epic_engine.swisskit.ui.theme.yellowBackground
import com.epic_engine.swisskit.ui.theme.yellowShopping

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
            color = blueFinance,
            colorBackground = blueBackground,
            destination = SwissKitDestination.Finance
        ),
        Tool(
            id = "contacts",
            name = "Contactos",
            description = "Gestiona contactos",
            icon = R.drawable.icon_contact,
            color = greenContact,
            colorBackground = greenBackground,
            destination = SwissKitDestination.Contacts
        ),
        Tool(
            id = "notes",
            name = "Notas",
            description = "Escribe tus notas",
            icon = R.drawable.icon_notes,
            color = purpleNotes,
            colorBackground = purpleBackground,
            destination = SwissKitDestination.Notes
        ),
        Tool(
            id = "qr_scanner",
            name = "Código QR",
            description = "Escanea / Genera",
            icon = R.drawable.icon_qr,
            color = pinkQRScanner,
            colorBackground = pinkBackground,
            destination = SwissKitDestination.QrScanner
        ),
        Tool(
            id = "converter",
            name = "Conversor",
            description = "Unidades / Divisas",
            icon = R.drawable.icon_converter,
            color = grayConverter,
            colorBackground = grayBackground,
            destination = SwissKitDestination.Converter
        ),
        Tool(
            id = "shopping",
            name = "Compras",
            description = "Lista de compras",
            icon = R.drawable.icon_shopping,
            color = yellowShopping,
            colorBackground = yellowBackground,
            destination = SwissKitDestination.Shopping
        )
    )
}
