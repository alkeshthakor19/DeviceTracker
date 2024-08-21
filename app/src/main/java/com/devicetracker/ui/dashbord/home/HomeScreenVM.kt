package com.devicetracker.ui.dashbord.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.devicetracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenVM @Inject constructor(private val repo: AuthRepository): ViewModel() {
    fun loadDevices() {

    }

    fun signOut() = repo.signOut()
}