package com.epic_engine.swisskit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
        }
        composable(SwissKitDestination.Shopping.route) {
        }
        composable(SwissKitDestination.Converter.route) {
        }
        composable(SwissKitDestination.Contacts.route) {
        }
        composable(SwissKitDestination.Finance.route) {
        }
        composable(SwissKitDestination.Notes.route) {
        }
        composable(SwissKitDestination.QrScanner.route) {
        }
    }
}
