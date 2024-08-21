package com.devicetracker.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.devicetracker.R
import com.devicetracker.TextFiledState
import com.devicetracker.ui.login.TextFieldError

@Composable
fun EmailFiled( emailState: TextFiledState) {
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
    //emailState.text ="alkeshthakor@gmail.com"
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
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        isError = passwordState.showError(),
        visualTransformation = PasswordVisualTransformation()
    )

    //passwordState.text = "alkesjsj"
}
