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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssetTrackerTheme {
                navController = rememberNavController()
                MainNavHost(mainNavHostController = navController)
            }
        }
    }
}