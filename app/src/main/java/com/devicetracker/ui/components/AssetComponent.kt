package com.devicetracker.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.devicetracker.R
import com.devicetracker.core.Constants
import com.devicetracker.ui.dashbord.member.Member


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerSpinner(
    memberList:List<Member>,
    selectedOwner: Member?,
    onOwnerSelected: (Member) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOwner?.memberName?:Constants.EMPTY_STR,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(id = R.string.str_asset_owner)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            memberList.forEach { member ->
                DropdownMenuItem(
                    text = { Text(member.memberName) },
                    onClick = {
                        onOwnerSelected(member)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AssetDescriptionField(description: TextFieldState) {
    OutlinedTextField(
        value = description.text,
        onValueChange = {
            description.text = it
        },
        label = {
            Text(text = stringResource(R.string.str_description_name))
        },
        modifier = Modifier
            .height(100.dp)
            .onFocusChanged {
                description.onFocusChange(it.isFocused)
//            if(!it.isFocused) {
//                assetName.enableShowError()  //TODO
//            }
            },
        isError = description.showError(),
        //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Go),
        singleLine = false,
        maxLines = 3
    )
    description.getError()?.let {error ->
        TextFieldError(textError = error )
    }
}

