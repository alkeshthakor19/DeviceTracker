package com.devicetracker.ui.login.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.devicetracker.core.Utils.Companion.showMessage
import com.devicetracker.domain.models.Response
import com.devicetracker.ui.ProgressBar
import com.devicetracker.ui.login.LoginViewModel

@Composable
fun SignIn(
    viewModel: LoginViewModel = hiltViewModel(),
    showErrorMessage: (errorMessage: String?) -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    when(val signInResponse = viewModel.loginInResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            val isSignedIn = signInResponse.data
            if (isSignedIn) {
                showMessage(context, "Login successfully!!")
                onLoginSuccess()
            }
            Unit
        }
        is Response.Failure -> signInResponse.apply {
            LaunchedEffect(e) {
                print(e)
                showErrorMessage(e.message)
            }
        }
    }
}