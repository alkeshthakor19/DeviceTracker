package com.devicetracker.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicetracker.domain.models.Response
import com.devicetracker.domain.repository.AuthRepository
import com.devicetracker.domain.repository.LoginInResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: AuthRepository
): ViewModel() {

    var loginInResponse by mutableStateOf<LoginInResponse>(Response.Success(false))
        private set

    fun login(email: String, password: String) = viewModelScope.launch {
        loginInResponse = Response.Loading
        loginInResponse = repo.firebaseSignInWithEmailAndPassword(email, password)
    }

}