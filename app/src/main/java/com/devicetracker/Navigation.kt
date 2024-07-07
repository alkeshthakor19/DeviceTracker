package com.devicetracker

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devicetracker.Destinations.DEVICE_DETAIL
import com.devicetracker.Destinations.HOME_ROUTE
import com.devicetracker.Destinations.LOGIN_ROUTE
import com.devicetracker.Destinations.MEMBER_DETAIL
import com.devicetracker.ui.device.DeviceDetailScreen
import com.devicetracker.ui.home.HomeRoute
import com.devicetracker.ui.login.LoginRoute
import com.devicetracker.ui.member.MemberProfileScreen

object Destinations {
    const val LOGIN_ROUTE = "login"
    const val HOME_ROUTE = "home"
    const val MEMBER_DETAIL = "member_detail/{userId}"
    const val DEVICE_DETAIL = "device_detail/{deviceId}"
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
           HomeRoute(navHostController)
        }

        composable(MEMBER_DETAIL,
              arguments = listOf(navArgument("userId"){ type = NavType.StringType })
            ) {
            MemberProfileScreen(
                memberId = it.arguments?.getString("userId")?:"1",
                onNavUp = {
                    navHostController.popBackStack()
                }
            )
        }

        composable(
            DEVICE_DETAIL,
            arguments = listOf(navArgument("deviceId"){ type = NavType.StringType })
        ) {
            DeviceDetailScreen(
                deviceId = it.arguments?.getString("deviceId")?:"1",
                onNavUp = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}
