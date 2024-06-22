package com.devicetracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeScreenVM(): ViewModel() {
    fun loadDevices() {

    }

    class HomeViewModeFactory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(HomeScreenVM::class.java)) {
                return HomeScreenVM() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}