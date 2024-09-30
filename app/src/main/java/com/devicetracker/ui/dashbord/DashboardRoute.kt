package com.devicetracker.ui.dashbord
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.devicetracker.R
import com.devicetracker.ui.AuthViewModel
import com.devicetracker.ui.Destinations.ASSETS
import com.devicetracker.ui.Destinations.ASSET_MODEL
import com.devicetracker.ui.Destinations.HOME
import com.devicetracker.ui.Destinations.LOGIN_ROUTE
import com.devicetracker.ui.Destinations.LOGOUT
import com.devicetracker.ui.Destinations.MEMBERS
import com.devicetracker.ui.dashbord.assets.AssetViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardRoute(mainNavHostController: NavHostController) {
     val assetViewModel : AssetViewModel = hiltViewModel()
     val isEditablePermission by assetViewModel.isAssetEditablePermission().observeAsState(false)
     val navigationController = rememberNavController()
     val authViewModel: AuthViewModel = hiltViewModel()
     val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

     ///List of Navigation Items that will be clicked
     val items = mutableListOf(
          NavigationItems(
               title = stringResource(id = R.string.str_home),
               selectedIcon = R.drawable.ic_home_black,
               unselectedIcon = R.drawable.ic_home_white,
               route = HOME
          ),
          NavigationItems(
               title = "Assets",
               selectedIcon = R.drawable.ic_devices,
               unselectedIcon = R.drawable.ic_devices_white,
               route = ASSETS
          ),
          NavigationItems(
               title = stringResource(id = R.string.str_member),
               selectedIcon = R.drawable.ic_baseline_users,
               unselectedIcon = R.drawable.ic_baseline_users_white,
               route = MEMBERS
          ),
          NavigationItems(
               title = stringResource(id = R.string.str_logout),
               selectedIcon = R.drawable.ic_logout_black,
               unselectedIcon = R.drawable.ic_logout_white,
               route = LOGOUT
          )
     )

     if(isEditablePermission) {
          items.add((items.size - 1), NavigationItems(
               title = stringResource(id = R.string.str_asset_models),
               selectedIcon = R.drawable.ic_devices_other,
               unselectedIcon = R.drawable.ic_devices_other_white,
               route = ASSET_MODEL
          ))
     }

     //Remember Clicked item state
     var selectedItemIndex by rememberSaveable {
          mutableStateOf(0)
     }

     val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

     val coroutineScope = rememberCoroutineScope()
     ModalNavigationDrawer(
          drawerState = drawerState,
          drawerContent = {
               ModalDrawerSheet {
                    Spacer(modifier = Modifier.height(16.dp))
                    items.forEachIndexed { index, item ->
                         NavigationDrawerItem(
                              label = { Text(text = item.title) },
                              selected = index == selectedItemIndex,
                              onClick = {
                                   selectedItemIndex = index
                                   coroutineScope.launch {
                                        drawerState.close()
                                   }
                                   if (item.route == LOGOUT){
                                        authViewModel.logout()
                                   } else {
                                        navigationController.navigate(items[selectedItemIndex].route) {
                                             popUpTo(navigationController.graph.id)
                                        }
                                   }
                              },
                              icon = {
                                   Icon(
                                        painter = painterResource(if (index == selectedItemIndex) { item.selectedIcon } else { item.unselectedIcon }),
                                        contentDescription = item.title
                                   )
                              },
                              badge = {
                                   item.badgeCount?.let {
                                        Text(text = item.badgeCount.toString())
                                   }
                              },
                              modifier = Modifier
                                   .padding(NavigationDrawerItemDefaults.ItemPadding)
                         )
                    }
               }
          },
     ){
          if (!isAuthenticated) {
               LaunchedEffect(Unit) {
                    mainNavHostController.navigate(LOGIN_ROUTE) {
                         popUpTo(mainNavHostController.graph.id) { inclusive = true }
                    }
               }
          }
          //Log.d("DashRoute", "DashboardNavHostContent call")
          DashboardNavHostContent(navigationController, drawerState)
     }
}