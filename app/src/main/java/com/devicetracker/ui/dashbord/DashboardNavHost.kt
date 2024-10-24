package com.devicetracker.ui.dashbord

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.devicetracker.R
import com.devicetracker.core.Constants
import com.devicetracker.ui.Destinations.ASSETS
import com.devicetracker.ui.Destinations.ASSETS_BY_TYPE
import com.devicetracker.ui.Destinations.ASSET_DETAIL
import com.devicetracker.ui.Destinations.ASSET_MODEL
import com.devicetracker.ui.Destinations.ASSET_SEARCH
import com.devicetracker.ui.Destinations.EDIT_ASSET
import com.devicetracker.ui.Destinations.EDIT_MEMBER
import com.devicetracker.ui.Destinations.FULL_IMAGE
import com.devicetracker.ui.Destinations.HOME
import com.devicetracker.ui.Destinations.MEMBERS
import com.devicetracker.ui.Destinations.MEMBER_DETAIL
import com.devicetracker.ui.Destinations.MEMBER_SEARCH
import com.devicetracker.ui.Destinations.NEW_ASSET
import com.devicetracker.ui.Destinations.NEW_MEMBER
import com.devicetracker.ui.components.FullScreenImage
import com.devicetracker.ui.dashbord.assets.AddAssetRoute
import com.devicetracker.ui.dashbord.assets.AssetDetailScreen
import com.devicetracker.ui.dashbord.assets.AssetEditScreen
import com.devicetracker.ui.dashbord.assets.AssetListByTypeScreen
import com.devicetracker.ui.dashbord.assets.AssetListScreen
import com.devicetracker.ui.dashbord.assets.AssetModelRoute
import com.devicetracker.ui.dashbord.assets.AssetSearchScreen
import com.devicetracker.ui.dashbord.home.HomeScreen
import com.devicetracker.ui.dashbord.member.AddMemberRoute
import com.devicetracker.ui.dashbord.member.MemberEditScreen
import com.devicetracker.ui.dashbord.member.MemberListScreen
import com.devicetracker.ui.dashbord.member.MemberProfileScreen
import com.devicetracker.ui.dashbord.member.MemberSearchScreen
import kotlinx.coroutines.launch

@Composable
fun DashboardNavHostContent(navigationController: NavHostController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    NavHost(navController = navigationController, startDestination = HOME) {
        composable(HOME) {
            //Log.d("DashNavHost", "Navigating to Home")
            HomeScreen(navHostController = navigationController) {
                coroutineScope.launch { drawerState.open() }
            }
        }
        composable(MEMBERS) {
            //Log.d("DashNavHost", "Navigating to Members")
            MemberListScreen(
                { coroutineScope.launch { drawerState.open() } }, navigationController
            )
        }
        composable(ASSETS) {
            //Log.d("DashNavHost", "Navigating to Assets")
            AssetListScreen(
                { coroutineScope.launch { drawerState.open() } }, navigationController
            )
        }
        composable(
            ASSETS_BY_TYPE,
            arguments = listOf(navArgument("assetType") { type = NavType.StringType })
        ) {
            //Log.d("DashNavHost", "Navigating to Assets by AssetType")
            AssetListByTypeScreen(
                assetType = it.arguments?.getString("assetType") ?: Constants.EMPTY_STR,
                navHostController = navigationController
            ) {
                navigationController.navigateUp()
            }
        }
        composable(ASSET_MODEL) {
            //Log.d("DashNavHost", "Navigating to Assets")
            AssetModelRoute {
                coroutineScope.launch { drawerState.open() }
            }

        }
        composable(NEW_MEMBER) {
            //Log.d("DashNavHost", "Navigating to New Member")
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
            //Log.d("DashNavHost", "Navigating to Member Detail")
            MemberProfileScreen(
                memberId = it.arguments?.getString("memberId") ?: Constants.EMPTY_STR,
                onNavUp = { navigationController.navigateUp() },
                navHostController = navigationController
            )
        }
        composable(
            EDIT_MEMBER,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType })
        ) {
            MemberEditScreen(
                memberId = it.arguments?.getString("memberId").toString(),
                onNavUp = {
                    navigationController.navigateUp()
                }
            )
        }
        composable(
            ASSET_DETAIL,
            arguments = listOf(navArgument("assetDocId") { type = NavType.StringType })
        ) {
            //Log.d("DashNavHost", "Navigating to Asset Detail")
            AssetDetailScreen(
                assetDocId = it.arguments?.getString("assetDocId")?:Constants.EMPTY_STR,
                onNavUp = {
                    navigationController.navigateUp()
                },
                navigationController
            )
        }
        composable(
            EDIT_ASSET,
            arguments = listOf(navArgument("assetDocId") { type = NavType.StringType })
        ) {
            //Log.d("DashNavHost", "Navigating to Edit Asset")
            AssetEditScreen(
                assetDocId = it.arguments?.getString("assetDocId").toString(),
                onNavUp = {
                    navigationController.navigateUp()
                }
            )
        }
        composable(MEMBER_SEARCH){
            MemberSearchScreen(navigationController){
                navigationController.navigateUp()
            }
        }
        composable(ASSET_SEARCH){
            AssetSearchScreen(navigationController){
                navigationController.navigateUp()
            }
        }
        composable(
            route = FULL_IMAGE,
            arguments = listOf(navArgument("imageUrl") { type = NavType.StringType }, navArgument("resourceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val imageUrl = backStackEntry.arguments?.getString("imageUrl")
            val resourceId = backStackEntry.arguments?.getInt("resourceId") ?: R.drawable.ic_devices
            FullScreenImage(imageUrl, resourceId){
                navigationController.navigateUp()
            }
        }
    }
}