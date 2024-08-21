package com.devicetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicetracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: AuthRepository): ViewModel() {
    init {
        getAuthState()
    }
    fun getAuthState() = repo.getAuthState(viewModelScope)
}