package com.devicetracker

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devicetracker.Destinations.HOME_ROUTE
import com.devicetracker.Destinations.LOGIN_ROUTE
import com.devicetracker.ui.home.HomeRoute
import com.devicetracker.ui.login.LoginRoute

object Destinations {
    const val LOGIN_ROUTE = "login"
    const val HOME_ROUTE = "home"
}

@Composable
fun DeviceTrackerRoute(
    navHostController: NavHostController =  rememberNavController()
) {
    NavHost(navController = navHostController, startDestination = LOGIN_ROUTE) {
        composable(LOGIN_ROUTE) {
                LoginRoute(onLoginSuccess = {
                    navHostController.navigate(HOME_ROUTE)
                })
        }
        composable(HOME_ROUTE) {
           HomeRoute()
        }
    }
}
