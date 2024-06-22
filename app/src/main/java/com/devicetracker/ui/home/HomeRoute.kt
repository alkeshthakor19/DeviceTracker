package com.devicetracker.ui.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeRoute() {
    val homeScreenVM: HomeScreenVM = viewModel(factory = HomeScreenVM.HomeViewModeFactory())
     HomeScreen()
}