package com.epic_engine.swisskit.feature.home.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.StickyNote2
import com.epic_engine.swisskit.navigation.SwissKitDestination
import com.epic_engine.swisskit.ui.theme.blueFinance
import com.epic_engine.swisskit.ui.theme.grayConverter
import com.epic_engine.swisskit.ui.theme.greenContact
import com.epic_engine.swisskit.ui.theme.orangeQrScanner
import com.epic_engine.swisskit.ui.theme.purpleNotes
import com.epic_engine.swisskit.ui.theme.yellowShopping

/**
 * Equivalente al enum ToolRoute de iOS + lista de herramientas de HomeView.
 * Catálogo estático de los 6 módulos de la app.
 */
object ToolCatalog {

    val all: List<Tool> = listOf(
        Tool(
            id = "shopping",
            name = "Lista de Compras",
            description = "Organiza y gestiona tu lista de compras",
            icon = Icons.Default.ShoppingCart,
            color = yellowShopping,
            destination = SwissKitDestination.Shopping
        ),
        Tool(
            id = "converter",
            name = "Conversor",
            description = "Convierte monedas y unidades de medida",
            icon = Icons.Default.CurrencyExchange,
            color = grayConverter,
            destination = SwissKitDestination.Converter
        ),
        Tool(
            id = "contacts",
            name = "Contactos",
            description = "Gestiona contactos organizados por categorías",
            icon = Icons.Default.Contacts,
            color = greenContact,
            destination = SwissKitDestination.Contacts
        ),
        Tool(
            id = "finance",
            name = "Finanzas",
            description = "Controla tus ingresos y gastos personales",
            icon = Icons.Default.AccountBalance,
            color = blueFinance,
            destination = SwissKitDestination.Finance
        ),
        Tool(
            id = "notes",
            name = "Notas",
            description = "Crea notas con formato Markdown y recordatorios",
            icon = Icons.Default.StickyNote2,
            color = purpleNotes,
            destination = SwissKitDestination.Notes
        ),
        Tool(
            id = "qr_scanner",
            name = "QR Scanner",
            description = "Escanea, genera y gestiona códigos QR",
            icon = Icons.Default.QrCodeScanner,
            color = orangeQrScanner,
            destination = SwissKitDestination.QrScanner
        )
    )
}
