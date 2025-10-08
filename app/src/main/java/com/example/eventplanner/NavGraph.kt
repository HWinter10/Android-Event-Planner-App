/**
 * Defines app navigation graph.
 * Sets up routes for HomeScreen, AddEventScreen, EditEventScreen.
 * Handles route arguments (e.g., eventId for EditEventScreen).
 * Provides type-safe navigation via sealed class Screen.
 */
package com.example.eventplanner

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventplanner.userint.AddEventScreen
import com.example.eventplanner.userint.EditEventScreen
import com.example.eventplanner.userint.HomeScreen
import com.example.eventplanner.viewmodel.EventViewModel
import com.example.eventplanner.userint.SplashScreen


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object AddEvent : Screen("add_event")
    object EditEvent : Screen("edit_event/{eventId}") {
        fun createRoute(eventId: Int) = "edit_event/$eventId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    eventViewModel: EventViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                eventViewModel = eventViewModel
            )
        }

        composable(Screen.AddEvent.route) {
            AddEventScreen(
                eventViewModel = eventViewModel,
                onEventAdded = { navController.popBackStack() }
            )
        }

        composable(Screen.EditEvent.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            eventId?.let {
                EditEventScreen(
                    navController = navController,
                    eventId = it,
                    eventViewModel = eventViewModel
                )
            }
        }
    }
}
