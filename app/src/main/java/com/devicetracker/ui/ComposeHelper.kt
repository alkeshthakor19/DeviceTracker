package com.devicetracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devicetracker.R
import com.devicetracker.ui.theme.AssetTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithTitleAndBackNavigation(
    titleText: String,
    onNavUp: () -> Unit
) {
    TopAppBar(
        title = { Text(text = titleText, style = MaterialTheme.typography.headlineMedium, color = AssetTrackerTheme.colors.textColor) },
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
