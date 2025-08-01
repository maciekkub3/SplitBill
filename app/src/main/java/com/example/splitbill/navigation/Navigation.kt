package com.example.splitbill.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.splitbill.ui.screens.addBillScreen.AddBillScreenRoot
import com.example.splitbill.ui.screens.addBillScreen.AddBillViewModel
import com.example.splitbill.ui.screens.billScreen.BillScreenRoot
import com.example.splitbill.ui.screens.billScreen.BillViewModel
import com.example.splitbill.ui.screens.mainScreen.MainScreenRoot
import com.example.splitbill.ui.screens.mainScreen.MainViewModel


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
            MainScreenRoot(
                navController = navController,
                viewModel = viewModel,
                )
        }
        composable<AddEventRoute> {
            val viewModel : AddBillViewModel = hiltViewModel()
            AddBillScreenRoot(
                navController = navController,
                viewModel = viewModel,

            )
        }
        composable<EventRoute> {
            val viewModel : BillViewModel = hiltViewModel()
            val billId = it.arguments?.getLong("billId")
                ?: throw IllegalArgumentException("billId is required")
            BillScreenRoot(
                navController = navController,
                viewModel = viewModel,
                billId = billId
            )
        }
    }
}
