package com.devicetracker.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.devicetracker.ui.Destinations.DASHBOARD_ROUTE
import com.devicetracker.ui.Destinations.LOGIN_ROUTE
import com.devicetracker.ui.theme.AssetTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssetTrackerTheme {
                navController = rememberNavController()
                MainNavHost(navHostController = navController)
                AuthState()
            }
        }
    }

    @Composable
    private fun AuthState() {
        val isUserSignedOut = viewModel.getAuthState().collectAsState().value
        Log.d("MainActivity", "nkp isUserSignedOut $isUserSignedOut")
        if (isUserSignedOut) {
            NavigateToSignInScreen()
        } else {
            NavigateToProfileScreen()
        }
    }

    @Composable
    private fun NavigateToSignInScreen() = navController.navigate(LOGIN_ROUTE) {
        popUpTo(navController.graph.id) {
            inclusive = true
        }
    }

    @Composable
    private fun NavigateToProfileScreen() = navController.navigate(DASHBOARD_ROUTE) {
        popUpTo(navController.graph.id) {
            inclusive = true
        }
    }
}



/*@Serializable
data class User(
    val Id: String,
    val Name: String,
    val Emp_Code: Int,
    val Emial: String,
    val Created_At: String,
)*/