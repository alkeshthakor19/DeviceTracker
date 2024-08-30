package com.devicetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.devicetracker.R

@Composable
fun TextFieldError(textError: String = "Invalid email") {
    Row( horizontalArrangement = Arrangement.Start) {
        Text(
            text = textError,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
fun EmailField(emailState: TextFieldState) {
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
            if(it.isFocused) {
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
}

@Composable
fun PasswordTextField(
    passwordState: TextFieldState
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
}

@Composable
fun EmployeeCodeField(employeeCode: TextFieldState) {
    OutlinedTextField(
        value = employeeCode.text,
        onValueChange = {
            employeeCode.text = it
        },
        label = {
            Text(text = stringResource(R.string.str_employee_code))
        },
        modifier = Modifier
            .onFocusChanged {
            employeeCode.onFocusChange(it.isFocused)
            if(it.isFocused) {
                employeeCode.enableShowError()
            }
        },
        isError = employeeCode.showError(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        singleLine = true
    )
    employeeCode.getError()?.let { error ->
        TextFieldError(textError = error, )
    }
}

@Composable
fun MemberNameField(memberName: TextFieldState) {
    OutlinedTextField(
        value = memberName.text,
        onValueChange = {
            memberName.text = it
        },
        label = {
            Text(text = stringResource(R.string.str_member_name))
        },
        modifier = Modifier.onFocusChanged {
            memberName.onFocusChange(it.isFocused)
            if(it.isFocused) {
                memberName.enableShowError()
            }
        },
        isError = memberName.showError(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        singleLine = true
    )
    memberName.getError()?.let {error ->
        TextFieldError(textError = error, )
    }
}

@Composable
fun MemberTypeCheckBox(memberWritablePermission: BooleanFieldState){
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = memberWritablePermission.isChecked,
            onCheckedChange = { memberWritablePermission.isChecked = it },
            colors = CheckboxDefaults.colors(Color.Green)
        )
        Text(text = stringResource(R.string.str_member_permission))
    }
}
