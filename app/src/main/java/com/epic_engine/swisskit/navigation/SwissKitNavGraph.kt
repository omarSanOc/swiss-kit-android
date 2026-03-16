package com.epic_engine.swisskit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.epic_engine.swisskit.feature.contacts.presentation.CategoriesScreen
import com.epic_engine.swisskit.feature.home.presentation.HomeScreen
import com.epic_engine.swisskit.feature.contacts.presentation.ContactsScreen
import com.epic_engine.swisskit.feature.converter.presentation.ConverterScreen
import com.epic_engine.swisskit.feature.finance.presentation.FinanceScreen
import com.epic_engine.swisskit.feature.notes.presentation.NoteDetailScreen
import com.epic_engine.swisskit.feature.notes.presentation.NotesScreen
import com.epic_engine.swisskit.feature.qrscanner.presentation.QRScannerScreen
import com.epic_engine.swisskit.feature.shopping.presentation.ShoppingScreen

@Composable
fun SwissKitNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: SwissKitDestination = SwissKitDestination.Home
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route
    ) {
        composable(SwissKitDestination.Home.route) {
            HomeScreen(
                onNavigateTo = { destination ->
                    navController.navigate(destination.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(SwissKitDestination.Shopping.route) {
            ShoppingScreen(
                onBack = { navController.navigateUp() }
            )
        }
        composable(SwissKitDestination.Converter.route) {
            ConverterScreen()
        }
        composable(SwissKitDestination.Contacts.route) {
            CategoriesScreen(
                onNavigateToContacts = { categoryId, categoryTitle ->
                    navController.navigate(
                        SwissKitDestination.ContactDetail.createRoute(categoryId, categoryTitle)
                    )
                }
            )
        }
        composable(
            route = SwissKitDestination.ContactDetail.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("categoryTitle") { type = NavType.StringType }
            )
        ) { backStack ->
            val categoryTitle = backStack.arguments?.getString("categoryTitle") ?: ""
            ContactsScreen(
                categoryTitle = categoryTitle,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable(SwissKitDestination.Finance.route) {
            FinanceScreen()
        }
        composable(SwissKitDestination.Notes.route) {
            NotesScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToCreate = {
                    navController.navigate(SwissKitDestination.NoteDetail.createRoute(null))
                },
                onNavigateToDetail = { noteId ->
                    navController.navigate(SwissKitDestination.NoteDetail.createRoute(noteId))
                }
            )
        }
        composable(
            route = SwissKitDestination.NoteDetail.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStack ->
            val noteId = backStack.arguments?.getString("noteId")?.takeIf { it != "new" }
            NoteDetailScreen(
                noteId = noteId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable(SwissKitDestination.QrScanner.route) {
            QRScannerScreen()
        }
    }
}
