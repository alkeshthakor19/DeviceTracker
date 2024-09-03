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
import com.devicetracker.R



@Composable
fun AssetNameField(assetName: TextFieldState) {
    OutlinedTextField(
        value = assetName.text,
        onValueChange = {
            assetName.text = it
        },
        label = {
            Text(text = stringResource(R.string.str_asset_name))
        },
        modifier = Modifier.onFocusChanged {
            assetName.onFocusChange(it.isFocused)
//            if(!it.isFocused) {
//                assetName.enableShowError()  //TODO
//            }
        },
        isError = assetName.showError(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        singleLine = true
    )
    assetName.getError()?.let {error ->
        TextFieldError(textError = error )
    }
}

