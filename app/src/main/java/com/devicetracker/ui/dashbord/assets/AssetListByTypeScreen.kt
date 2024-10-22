package com.devicetracker.ui.dashbord.assets

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.devicetracker.R
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.NoDataMessage
import com.devicetracker.ui.getFontSizeByPercent

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AssetListByTypeScreen(assetType: String, navHostController: NavHostController, onNavUp: () -> Unit) {
    val assetViewModel: AssetViewModel = hiltViewModel()
    // Observe LiveData from ViewModel
    val assets by assetViewModel.assets.observeAsState(initial = emptyList())
    var assetsFilter by rememberSaveable { mutableStateOf(assets) }
    val pullToRefreshState = rememberPullToRefreshState()
    val onRefreshAsset: () -> Unit = {
        assetViewModel.refreshAssetsByAssetType(assetType)
        assetsFilter = assets
    }
    val isEditablePermission by assetViewModel.isAssetEditablePermission().observeAsState(false)
    LaunchedEffect(Unit) {
        assetViewModel.refreshAssetsByAssetType(assetType)
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    //val isWorkingAsset = remember { mutableStateOf(false) }
    assetsFilter = assets
    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(
                titleText = stringResource(id = R.string.str_asset_list),
                onNavUp= onNavUp,
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter Menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        properties = PopupProperties(focusable = true)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Working", fontSize = getFontSizeByPercent(fontSizeInPercent = 3.5f)) },
                            onClick = {
                                expanded = false
                                assetsFilter = assets.filter { it.assetWorkingStatus }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Not Working", fontSize = getFontSizeByPercent(fontSizeInPercent = 3.5f)) },
                            onClick = {
                                expanded = false
                                assetsFilter = assets.filter { !it.assetWorkingStatus }
                            }
                        )
                    }
                }
            )
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
            val workingAssets = assets.filter { asset -> asset.assetWorkingStatus }
            Column(modifier = Modifier.fillMaxSize()) {
                Text(modifier = Modifier.fillMaxWidth(), text = "No. of working assets is: ${workingAssets.size}", fontSize = getFontSizeByPercent(fontSizeInPercent = 4f), color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if(assetsFilter.isEmpty()){
                        item {
                            NoDataMessage()
                        }
                    } else{
                        items(assetsFilter) { asset ->
                            AssetRow(asset) { assetId ->
                                navHostController.navigate("asset_detail/${assetId}")
                            }
                        }
                    }
                }
            }
        }
    }
}