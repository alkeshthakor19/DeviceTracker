package com.devicetracker.ui.dashbord.assets

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devicetracker.R
import com.devicetracker.core.Constants.INT_SIZE_72
import com.devicetracker.noDoubleClick
import com.devicetracker.ui.AppFloatingButton
import com.devicetracker.ui.Destinations.ASSET_SEARCH
import com.devicetracker.ui.Destinations.NEW_ASSET
import com.devicetracker.ui.components.LabelAndTextWithColor
import com.devicetracker.ui.components.NoDataMessage
import com.devicetracker.ui.dashbord.member.Member
import com.devicetracker.ui.dashbord.member.MemberViewModel
import com.devicetracker.ui.theme.AssetTrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AssetListScreen(openDrawer: () -> Unit, navHostController: NavHostController) {
    val assetViewModel: AssetViewModel = hiltViewModel()
    // Observe LiveData from ViewModel
    val assets by assetViewModel.assets.observeAsState(initial = emptyList())
    val pullToRefreshState = rememberPullToRefreshState()
    val onRefreshAsset: () -> Unit = {
        assetViewModel.refreshAssets()
    }
    val isEditablePermission by assetViewModel.isAssetEditablePermission().observeAsState(false)
    LaunchedEffect(Unit) {
        assetViewModel.refreshAssets()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.str_asset_list),  style = MaterialTheme.typography.headlineMedium, color = AssetTrackerTheme.colors.textColor) },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = stringResource(id = R.string.str_menu))
                    }
                },
                actions = {
                    IconButton(onClick = { navHostController.navigate(ASSET_SEARCH) }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = stringResource(id = R.string.str_search))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = Color.Black,
                ),
            )
        },
        floatingActionButton = {
            if(isEditablePermission) {
                AppFloatingButton {
                    navHostController.navigate(NEW_ASSET)
                }
            }
        }
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
            isRefreshing = assetViewModel.isLoaderShowing,
            onRefresh = onRefreshAsset,
            state = pullToRefreshState,
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if(assets.isEmpty()){
                    item {
                        NoDataMessage()
                    }
                } else{
                    items(assets) { asset ->
                        AssetRow(asset) { assetId ->
                            navHostController.navigate("asset_detail/${assetId}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssetRow(asset: Asset, navigateDeviceDetailCallBack: (String)-> Unit) {
    ElevatedCard (
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        colors = CardDefaults.cardColors(containerColor = AssetTrackerTheme.colors.cardBackgroundColor)
    ){
        Row(
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
                .noDoubleClick { navigateDeviceDetailCallBack.invoke(asset.assetDocId.toString()) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            AssetPicture(asset, INT_SIZE_72)
            AssetContent(asset)
        }
    }
}

@Composable
fun AssetPicture(asset: Asset, imageSize: Int) {
    Card(
        shape = RoundedCornerShape(5.dp),
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(asset.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(resourceId),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .size(imageSize.dp)
        )
    }
}

@Composable
fun AssetContent(asset: Asset) {
    val assetTypeName = when(asset.assetType) {
        AssetType.TAB.name -> stringResource(id = R.string.str_device_type_tab)
        AssetType.USB.name -> stringResource(id = R.string.str_device_type_storage)
        AssetType.CABLE.name -> stringResource(id = R.string.str_device_type_cable)
        AssetType.PROBE.name -> stringResource(id = R.string.str_device_type_probe)
        else -> stringResource(id = R.string.str_device_type_other)
    }
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
         text = asset.assetName,
         style = MaterialTheme.typography.titleMedium
        )
        LabelAndTextWithColor(labelText = stringResource(id = R.string.str_asset_type), normalText = assetTypeName, color = Color.Gray)
        LabelAndTextWithColor(labelText = stringResource(id = R.string.str_label_asset_model_name), normalText = asset.modelName.toString(), color = Color.Gray)
    }
}
