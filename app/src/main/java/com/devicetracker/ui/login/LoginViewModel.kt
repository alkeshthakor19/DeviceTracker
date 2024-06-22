package com.devicetracker.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginViewModel(): ViewModel() {

    fun login(email: String, password: String): Boolean {
            return (email.isNotEmpty() && password.isNotEmpty())
    }

    class LoginViewModelFactory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}