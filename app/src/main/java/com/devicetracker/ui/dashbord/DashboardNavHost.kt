package com.devicetracker.ui.dashbord

import android.util.Log
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.devicetracker.ui.Destinations.ASSETS
import com.devicetracker.ui.Destinations.ASSET_DETAIL
import com.devicetracker.ui.Destinations.HOME
import com.devicetracker.ui.Destinations.MEMBERS
import com.devicetracker.ui.Destinations.MEMBER_DETAIL
import com.devicetracker.ui.Destinations.NEW_ASSET
import com.devicetracker.ui.Destinations.NEW_MEMBER
import com.devicetracker.ui.dashbord.assets.AddAssetRoute
import com.devicetracker.ui.dashbord.assets.AssetDetailScreen
import com.devicetracker.ui.dashbord.assets.AssetListScreen
import com.devicetracker.ui.dashbord.home.HomeScreen
import com.devicetracker.ui.dashbord.member.AddMemberRoute
import com.devicetracker.ui.dashbord.member.MemberListScreen
import com.devicetracker.ui.dashbord.member.MemberProfileScreen
import kotlinx.coroutines.launch

@Composable
fun DashboardNavHostContent(navigationController: NavHostController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    NavHost(navController = navigationController, startDestination = HOME) {
        composable(HOME) {
            Log.d("DashNavHost", "nkp Navigating to Home")
            HomeScreen {
                coroutineScope.launch { drawerState.open() }
            }
        }
        composable(MEMBERS) {
            Log.d("DashNavHost", "nkp Navigating to Members")
            MemberListScreen(
                { coroutineScope.launch { drawerState.open() } }, navigationController
            )
        }
        composable(ASSETS) {
            Log.d("DashNavHost", "nkp Navigating to Assets")
            AssetListScreen(
                { coroutineScope.launch { drawerState.open() } }, navigationController
            )
        }
        composable(NEW_MEMBER) {
            Log.d("DashNavHost", "nkp Navigating to New Member")
            AddMemberRoute {
                navigationController.navigateUp()
            }
        }
        composable(NEW_ASSET){
            AddAssetRoute {
                navigationController.navigateUp()
            }
        }
        composable(
            MEMBER_DETAIL,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType })
        ) {
            Log.d("DashNavHost", "nkp Navigating to Member Detail")
            MemberProfileScreen(
                memberId = it.arguments?.getString("memberId") ?: "",
                onNavUp = { navigationController.navigateUp() }
            )
        }
        composable(
            ASSET_DETAIL,
            arguments = listOf(navArgument("assetId") { type = NavType.StringType })
        ) {
            Log.d("DashNavHost", "nkp Navigating to Asset Detail")
            AssetDetailScreen(
                assetId = it.arguments?.getString("assetId")?:"",
                onNavUp = {
                    navigationController.navigateUp()
                }
            )
        }
    }
}