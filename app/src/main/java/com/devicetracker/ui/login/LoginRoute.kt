package com.devicetracker.ui.login

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.devicetracker.core.Utils.Companion.showMessage
import com.devicetracker.ui.login.component.SignIn


@Composable
fun LoginRoute() {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()
    LoginScreen(
        onSignInButtonCLick = { email, password ->
            Log.d("Alkesh","Input email is: $email and password is: $password")
            loginViewModel.login(email,password)
        }
    )
    SignIn(
        showErrorMessage = { errorMessage ->
            showMessage(context, errorMessage)
        }
    )
}