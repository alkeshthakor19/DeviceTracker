package com.devicetracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicetracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithTitleAndBackNavigation(
    titleText: String,
    onNavUp: () -> Unit
) {
    TopAppBar(
        title = { Text(text = titleText, style = MaterialTheme.typography.headlineMedium) },
        navigationIcon = {
            IconButton(onClick = onNavUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.app_name)
                )
            }
        }
    )
}

@Composable
fun AppFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = { onClick() }, modifier = Modifier.padding(bottom = 24.dp) ) {
        Icon(Icons.Filled.Add, contentDescription ="Floating Action Button" )
    }
}