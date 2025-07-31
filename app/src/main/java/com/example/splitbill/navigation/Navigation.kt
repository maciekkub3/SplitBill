package com.example.splitbill.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.splitbill.ui.Screens.AddBillScreen.AddBillScreen
import com.example.splitbill.ui.Screens.AddBillScreen.AddBillViewModel
import com.example.splitbill.ui.Screens.BillScreen.BillScreen
import com.example.splitbill.ui.Screens.BillScreen.BillViewModel
import com.example.splitbill.ui.Screens.MainScreen.MainScreen
import com.example.splitbill.ui.Screens.MainScreen.MainViewModel


@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            val viewModel : MainViewModel = hiltViewModel()
            MainScreen(
                navController = navController,
                viewModel = viewModel,
                )
        }
        composable<AddEventRoute> {
            val viewModel : AddBillViewModel = hiltViewModel()
            AddBillScreen(
                navController = navController,
                viewModel = viewModel,

            )
        }
        composable<EventRoute> {
            val viewModel : BillViewModel = hiltViewModel()
            val billId = it.arguments?.getLong("billId")
                ?: throw IllegalArgumentException("billId is required")
            BillScreen(
                navController = navController,
                viewModel = viewModel,
                billId = billId
            )
        }
    }
}
