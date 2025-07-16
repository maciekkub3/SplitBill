package com.example.splitbill.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute.route
    ) {
        composable<HomeRoute> {
            // HomeScreen(navController = navController, state = state)
        }
        composable<AddEventRoute> {
            // AddEventScreen(navController = navController, state = state)
        }
        composable<EventRoute> {
            // EventScreen(navController = navController, state = state)
        }
    }

}
