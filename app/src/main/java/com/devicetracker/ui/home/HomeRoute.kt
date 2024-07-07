package com.devicetracker.ui.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun HomeRoute(navHostController: NavHostController) {
    val homeScreenVM: HomeScreenVM = viewModel(factory = HomeScreenVM.HomeViewModeFactory())
     HomeScreen(navHostController)
}