package com.devicetracker.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devicetracker.EmailState
import com.devicetracker.PasswordState
import com.devicetracker.R
import com.devicetracker.TextFiledState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    onSignInButtonCLick: (email: String, password: String) -> Unit
) {
    Scaffold (modifier = Modifier.fillMaxSize()){
       MainContent( onSignInButtonCLick = onSignInButtonCLick)
    }
}

//@Preview(showBackground = true)
@Composable
fun MainContent(onSignInButtonCLick: (email: String, password: String) -> Unit) {

    val emailState = remember { EmailState() }
    val passwordState = remember { PasswordState() }

    Column (
        Modifier
            .fillMaxSize()
            .absolutePadding(16.dp, 64.dp, 16.dp, 16.dp),
        horizontalAlignment =  Alignment.CenterHorizontally) {

        val onSignInAction = {
            if(!emailState.isValid) {
                emailState.enableShowError()
            } else if(!passwordState.isValid) {
                 passwordState.enableShowError()
            } else {
                onSignInButtonCLick(emailState.text,passwordState.text)
            }
        }

        Text(text = stringResource(R.string.str_welcome_to_device_tracker),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(160.dp))
        EmailFiled(emailState)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordTextFiled(passwordState)
        Spacer(modifier = Modifier.height(36.dp))
        Button(onClick = onSignInAction) {
            Text(text ="Login", fontSize = 18.sp, modifier = Modifier.absolutePadding(8.dp,2.dp,8.dp,2.dp))
        }
    }
}

@Composable
fun EmailFiled( emailState: TextFiledState ) {
    OutlinedTextField(
        value = emailState.text,
        onValueChange = {
            emailState.text = it
        },
        label = {
            Text(text = stringResource(R.string.str_email_address))
        },
        modifier = Modifier.onFocusChanged {
                  emailState.onFocusChange(it.isFocused)
                  if(!it.isFocused) {
                      emailState.enableShowError()
                  }
        },
        isError = emailState.showError(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        singleLine = true
    )
    emailState.getError()?.let {error ->
        TextFieldError(textError = error, )
    }
    emailState.text ="alkeshthakor@gmail.com"
}

@Composable
fun PasswordTextFiled(
    passwordState: TextFiledState
) {
    OutlinedTextField(
        value = passwordState.text,
        onValueChange = {
            passwordState.text = it
        },
        label = {
            Text(text = stringResource(R.string.str_password))
        },
        isError = passwordState.showError(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        singleLine = true
    )

    passwordState.text = "alkesjsj"
}

@Composable
fun TextFieldError(textError: String = "Invalid email") {
    Text(
        text = textError,
        color = MaterialTheme.colorScheme.error,
    )
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(onSignInButtonCLick = {_,_->})
}