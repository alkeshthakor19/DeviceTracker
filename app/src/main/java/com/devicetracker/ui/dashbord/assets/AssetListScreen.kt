package com.devicetracker.ui.dashbord.assets

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.devicetracker.R
import com.devicetracker.noDoubleClick
import com.devicetracker.ui.AppFloatingButton
import com.devicetracker.ui.Destinations.NEW_ASSET
import com.devicetracker.ui.ProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AssetListScreen(openDrawer: () -> Unit, navHostController: NavHostController) {
    val assetViewModel: NewAssetViewModel = hiltViewModel()
    val assets by assetViewModel.assets.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asset List",  style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = Color.Black,
                ),
            )
        },
        floatingActionButton = {
            AppFloatingButton {
                navHostController.navigate(NEW_ASSET)
            }
        }
    ) {
        if (assetViewModel.isLoaderShowing) {
            ProgressBar()
        } else {
            LazyColumn(modifier = Modifier.padding(top = it.calculateTopPadding())) {
                items(assets) {
                    AssetRow(it) { assetId ->
                        navHostController.navigate("asset_detail/${assetId}")
                    }
                }
            }
        }
    }
}

@Composable
fun AssetRow(asset: Asset, navigateDeviceDetailCallBack: (String)-> Unit) {
    ElevatedCard (
        shape = CutCornerShape(topEnd = 24.dp, bottomStart = 24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ){
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
                .noDoubleClick { navigateDeviceDetailCallBack.invoke(asset.assetId ?: "") },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            AssetTypePicture(asset)
            AssetContent(asset)
        }
    }
}

@Composable
fun AssetTypePicture(asset: Asset) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    ) {
        val resourceId = when(asset.assetType) {
            AssetType.TAB.name -> R.drawable.ic_devices
            AssetType.CABLE.name -> R.drawable.ic_baseline_cable
            else -> R.drawable.ic_devices_other
        }
        Image(
            painter = painterResource(id = resourceId),
            modifier = Modifier.size(76.dp),
            contentDescription = stringResource(id = R.string.app_name),
            contentScale = ContentScale.Inside)
    }
}

@Composable
fun AssetContent(asset: Asset) {
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
         Text(
             text = asset.assetName,
             style = MaterialTheme.typography.titleLarge
             )
           val assetTypeName = when(asset.assetType) {
               AssetType.TAB.name -> stringResource(id = R.string.str_device_type_tab)
               AssetType.USB.name -> stringResource(id = R.string.str_device_type_storage)
               AssetType.CABLE.name -> stringResource(id = R.string.str_device_type_cable)
               AssetType.PROBE.name -> stringResource(id = R.string.str_device_type_probe)
               else -> stringResource(id = R.string.str_device_type_other)
           }
           Row {
              Text(text = stringResource(id = R.string.str_asset_type), color = Color.Gray)
              Text(text = assetTypeName)
           }
           Row {
              Text(text = stringResource(id = R.string.str_asset_model), color = Color.Gray)
              Text(text = asset.model.toString())
           }
    }
}
