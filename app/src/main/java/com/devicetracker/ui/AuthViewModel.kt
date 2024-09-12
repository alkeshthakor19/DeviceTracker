package com.devicetracker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicetracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: AuthRepository): ViewModel() {
    var isLoaderShowing by mutableStateOf(true)
        private set

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    init {
        checkIfUserIsAuthenticated()
    }

    private fun checkIfUserIsAuthenticated() {
        viewModelScope.launch {
            _isAuthenticated.value = repo.currentUser != null
        }
    }

    fun logout() {
        repo.signOut()
        _isAuthenticated.value = false
    }
}