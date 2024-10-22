package com.devicetracker.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.devicetracker.R
import com.devicetracker.ui.theme.AssetTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithTitleAndBackNavigation(
    titleText: String,
    onNavUp: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = titleText, style = MaterialTheme.typography.headlineMedium, fontSize = getFontSizeByPercent(fontSizeInPercent = 5f), color = AssetTrackerTheme.colors.textColor) },
        navigationIcon = {
            IconButton(onClick = onNavUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.app_name)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = Color.Black,
        ),
        actions = actions
    )
}

@Composable
fun AppFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = { onClick() }, modifier = Modifier.padding(bottom = 24.dp) ) {
        Icon(Icons.Filled.Add, contentDescription ="Floating Action Button" )
    }
}

@Composable
fun ProgressBar() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}

@Composable
fun getFontSizeByPercent(fontSizeInPercent: Float): TextUnit {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    return (screenWidth * (fontSizeInPercent/100)).sp  // Calculate font size as x% of screen width
}

@Composable
fun getWidthInPercent(widthInPercent: Float): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    return (screenWidth * (widthInPercent/100)).dp
}

@Composable
fun isLandScapeMode(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = title, fontSize = getFontSizeByPercent(fontSizeInPercent = 4f)) },
            text = { Text(text = message, fontSize = getFontSizeByPercent(fontSizeInPercent = 3.5f)) },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text(stringResource(id = R.string.str_delete), fontSize = getFontSizeByPercent(fontSizeInPercent = 4f), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.str_cancel), fontSize = getFontSizeByPercent(fontSizeInPercent = 4f))
                }
            }
        )
    }
}

@Composable
fun ImagePickUpDialog(
    title: String,
    message: String,
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit
) {
    if (isDialogOpen) {
        AlertDialog(
            title = {
                Text(text = title, fontSize = getFontSizeByPercent(fontSizeInPercent = 4f))
            },
            text = {
                Text(text = message, fontSize = getFontSizeByPercent(fontSizeInPercent = 3.5f), lineHeight = 1.2.em)
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onCamera()
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(id = R.string.str_camera), fontSize = getFontSizeByPercent(fontSizeInPercent = 4f))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onGallery()
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(id = R.string.str_gallery), fontSize = getFontSizeByPercent(fontSizeInPercent = 4f))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T>CustomDropDownMenu(
    label: String,
    selectedString: String,
    isEditable: Boolean,
    itemList: List<T>,
    isExpanded: MutableState<Boolean>,
    itemToString: (T) -> String,
    onSelectedItem: (T) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = isExpanded.value,
        onExpandedChange = {
            if (isEditable) {
                isExpanded.value = !isExpanded.value
            }
        }
    ) {
        OutlinedTextField(
            value = selectedString,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .menuAnchor(),
            enabled = isEditable
        )
        ExposedDropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = { isExpanded.value = false }
        ) {
            itemList.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemToString(item)) },
                    onClick = {
                        onSelectedItem(item)
                        isExpanded.value = false
                    },
                    enabled = isEditable
                )
            }
        }
    }
}