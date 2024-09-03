package com.devicetracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.devicetracker.ui.Destinations.DASHBOARD_ROUTE
import com.devicetracker.ui.Destinations.LOGIN_ROUTE
import com.devicetracker.ui.dashbord.DashboardRoute
import com.devicetracker.ui.login.LoginRoute

object Destinations {
    const val LOGIN_ROUTE = "login"
    const val DASHBOARD_ROUTE = "dashboard"
    const val HOME = "home"
    const val MEMBERS = "members"
    const val NEW_MEMBER = "new_member"
    const val MEMBER_DETAIL = "member_detail/{memberId}"
    const val ASSETS = "assets"
    const val NEW_ASSET = "new_asset"
    const val ASSET_DETAIL = "asset_detail/{assetId}"
    const val LOGOUT = "logout"
}

@Composable
fun MainNavHost(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = LOGIN_ROUTE) {
        composable(LOGIN_ROUTE) {
            LoginRoute {
                navHostController.navigate(DASHBOARD_ROUTE) {
                    // Clear the navigation stack
                    popUpTo(LOGIN_ROUTE) { inclusive = true }
                }
            }
        }
        composable(DASHBOARD_ROUTE) {
            DashboardRoute()
        }
    }
}
