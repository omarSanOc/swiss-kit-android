package com.epic_engine.swisskit.navigation

sealed class SwissKitDestination(val route: String) {
    data object Home : SwissKitDestination("home")
    data object Shopping : SwissKitDestination("shopping")
    data object Converter : SwissKitDestination("converter")
    data object Contacts : SwissKitDestination("contacts")
    data object Finance : SwissKitDestination("finance")
    data object Notes : SwissKitDestination("notes")
    data object QrScanner : SwissKitDestination("qr_scanner")
}
