package com.devicetracker.ui.login

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun LoginRoute(onLoginSuccess:() -> Unit) {
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.LoginViewModelFactory())
    LoginScreen(
        onSignInButtonCLick = { email, password ->
           Log.d("Alkesh","Input email is: $email and password is: $password")
            val isLoginSuccess = loginViewModel.login(email,password)
            if(isLoginSuccess) {
              onLoginSuccess()
            }
        }
    )
}