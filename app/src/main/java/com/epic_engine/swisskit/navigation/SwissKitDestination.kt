package com.epic_engine.swisskit.navigation

import android.net.Uri

sealed class SwissKitDestination(val route: String) {
    data object Home : SwissKitDestination("home")
    data object Shopping : SwissKitDestination("shopping")
    data object Converter : SwissKitDestination("converter")
    data object Contacts : SwissKitDestination("contacts")
    data object Finance : SwissKitDestination("finance")
    data object EditFinance : SwissKitDestination("finance/edit/{financeId}") {
        fun createRoute(financeId: String?) = "finance/edit/${financeId ?: "new"}"
    }
    data object Notes : SwissKitDestination("notes")
    data object QrScanner : SwissKitDestination("qr_scanner")
    data object NoteDetail : SwissKitDestination("note_detail/{noteId}") {
        fun createRoute(noteId: String?) = "note_detail/${noteId ?: "new"}"
    }
    data object ContactDetail : SwissKitDestination("contact_detail/{categoryId}/{categoryTitle}") {
        fun createRoute(categoryId: String, categoryTitle: String) =
            "contact_detail/$categoryId/${Uri.encode(categoryTitle)}"
    }
}
