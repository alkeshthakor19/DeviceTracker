package com.devicetracker.ui.dashbord

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.devicetracker.fromJson
import com.devicetracker.ui.Destinations.ASSETS
import com.devicetracker.ui.Destinations.ASSET_DETAIL
import com.devicetracker.ui.Destinations.HOME
import com.devicetracker.ui.Destinations.MEMBERS
import com.devicetracker.ui.Destinations.MEMBER_DETAIL
import com.devicetracker.ui.Destinations.NEW_MEMBER
import com.devicetracker.ui.dashbord.assets.AssetDetailScreen
import com.devicetracker.ui.dashbord.assets.DeviceListScreen
import com.devicetracker.ui.dashbord.home.HomeScreen
import com.devicetracker.ui.dashbord.member.AddMemberRoute
import com.devicetracker.ui.dashbord.member.Member
import com.devicetracker.ui.dashbord.member.MemberListScreen
import com.devicetracker.ui.dashbord.member.MemberProfileScreen
import kotlinx.coroutines.launch

@Composable
fun DashboardNavHost(navigationController: NavHostController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    NavHost(navController = navigationController, startDestination = HOME) {
        composable(HOME) {
            HomeScreen {
                coroutineScope.launch { drawerState.open() }
            }
        }
        composable(MEMBERS) {
            MemberListScreen(
                { coroutineScope.launch { drawerState.open() } }, navigationController
            )
        }
        composable(ASSETS) {
            DeviceListScreen(
                { coroutineScope.launch { drawerState.open() } }, navigationController
            )
        }

        composable(NEW_MEMBER) {
            AddMemberRoute{
                navigationController.navigateUp()
            }
        }

        composable(
            MEMBER_DETAIL,
            arguments = listOf(navArgument("memberString"){ type = NavType.StringType })
        ) {
            it.arguments?.getString("memberString")?.let { jsonString ->
                val member = jsonString.fromJson(Member::class.java)
                MemberProfileScreen(
                    memberData = member,
                    onNavUp = {
                        navigationController.navigateUp()
                    }
                )
            }
        }

        composable(
            ASSET_DETAIL,
            arguments = listOf(navArgument("deviceId"){ type = NavType.StringType })
        ) {
            AssetDetailScreen(
                deviceId = it.arguments?.getString("deviceId")?:"1",
                onNavUp = {
                    navigationController.navigateUp()
                }
            )
        }
    }
}