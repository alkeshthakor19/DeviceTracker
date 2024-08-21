package com.devicetracker.ui.dashbord

import com.devicetracker.ui.Destinations.HOME

data class NavigationItems(
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val badgeCount: Int? = null,
    val route: String = HOME
)