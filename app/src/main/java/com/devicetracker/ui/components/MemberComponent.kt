package com.devicetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
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
        modifier = Modifier.fillMaxWidth(0.95f)
            .onFocusChanged {
            emailState.onFocusChange(it.isFocused)
            /*if(it.isFocused) {
                emailState.enableShowError()
            }*/
        },
        isError = emailState.showError(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        singleLine = true
    )
    emailState.getError(stringResource(R.string.str_email_address))?.let { error ->
        TextFieldError(textError = error, )
    }
}

@Composable
fun PasswordTextField(
    passwordState: TextFieldState
) {
    OutlinedTextField(
        value = passwordState.text,
        modifier = Modifier.fillMaxWidth(0.95f) .onFocusChanged {
            passwordState.onFocusChange(it.isFocused)
        },
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
    passwordState.getError(stringResource(R.string.str_password))?.let { error ->
        TextFieldError(textError = error, )
    }
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
            .fillMaxWidth(0.95f)
            .onFocusChanged {
            employeeCode.onFocusChange(it.isFocused)
            /*if(it.isFocused) {
                employeeCode.enableShowError()
            }*/
        },
        isError = employeeCode.showError(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        singleLine = true
    )
    employeeCode.getError(stringResource(R.string.str_employee_code))?.let { error ->
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
        modifier = Modifier.fillMaxWidth(0.95f)
            .onFocusChanged {
            memberName.onFocusChange(it.isFocused)
            /*if(it.isFocused) {
                memberName.enableShowError()
            }*/
        },
        isError = memberName.showError(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        singleLine = true
    )
    memberName.getError(stringResource(R.string.str_member_name))?.let { error ->
        TextFieldError(textError = error, )
    }
}

@Composable
fun MemberEditableCheckBox(memberEditablePermission: BooleanFieldState){
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 0.dp).fillMaxWidth(0.95f)
    ) {
        Checkbox(
            checked = memberEditablePermission.isChecked,
            onCheckedChange = { memberEditablePermission.isChecked = it },
            colors = CheckboxDefaults.colors(Color.Green)
        )
        Text(text = stringResource(R.string.str_member_permission))
    }
}

@Composable
fun AssetEditableCheckBox(assetEditablePermission: BooleanFieldState){
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 0.dp).fillMaxWidth(0.95f)
    ) {
        Checkbox(
            checked = assetEditablePermission.isChecked,
            onCheckedChange = { assetEditablePermission.isChecked = it },
            colors = CheckboxDefaults.colors(Color.Green)
        )
        Text(text = stringResource(R.string.str_member_permission))
    }
}

@Composable
fun MemberMobileField(empMobileNo: TextFieldState) {
    OutlinedTextField(
        value = empMobileNo.text,
        onValueChange = {
            empMobileNo.text = it
        },
        label = {
            Text(text = stringResource(R.string.str_mobile_number))
        },
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .onFocusChanged {
                empMobileNo.onFocusChange(it.isFocused)
            },
        isError = empMobileNo.showError(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
        singleLine = true
    )
    empMobileNo.getError(stringResource(R.string.str_mobile_number))?.let { error ->
        TextFieldError(textError = error, )
    }
}

@Composable
fun MemberEmailRowSection(labelText: String, valueText: String, iconSize: Int, fontSize:TextUnit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.Email, contentDescription = labelText, modifier = Modifier.size(iconSize.dp), tint = Color.Gray)
        Text(text = " $valueText", fontSize = fontSize, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
