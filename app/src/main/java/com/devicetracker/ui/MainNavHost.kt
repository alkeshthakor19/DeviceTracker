package com.devicetracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
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
    const val ASSET_DETAIL = "asset_detail/{assetDocId}"
    const val EDIT_ASSET = "edit_asset/{assetDocId}"
    const val ASSET_MODEL = "asset_model"
    const val MEMBER_SEARCH = "member_search"
    const val ASSET_SEARCH = "asset_search"
    const val LOGOUT = "logout"
}

@Composable
fun MainNavHost(mainNavHostController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val startDestinationRoute = if(isAuthenticated) DASHBOARD_ROUTE else LOGIN_ROUTE
    NavHost( navController = mainNavHostController, startDestination = startDestinationRoute ) {
        composable(LOGIN_ROUTE) {
            LoginRoute {
                mainNavHostController.navigate(DASHBOARD_ROUTE) {
                    // Clear the navigation stack
                    popUpTo(LOGIN_ROUTE) { inclusive = true }
                }
            }
        }
        composable(DASHBOARD_ROUTE) {
            DashboardRoute(mainNavHostController)
        }
    }
}
