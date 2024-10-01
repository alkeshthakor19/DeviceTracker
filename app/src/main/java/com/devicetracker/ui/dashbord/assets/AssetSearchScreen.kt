package com.devicetracker.ui.dashbord.assets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.devicetracker.R
import com.devicetracker.core.Constants
import com.devicetracker.ui.components.CustomSearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetSearchScreen(navHostController: NavHostController, onNavUp: () -> Unit){
    val assetViewModel: AssetViewModel = hiltViewModel()
    var text by rememberSaveable { mutableStateOf(Constants.EMPTY_STR) }
    val assets by assetViewModel.assets.observeAsState(emptyList())
    var assetsFilter: List<Asset>
    LaunchedEffect(Unit) {
        assetViewModel.refreshAssets()
    }
    Scaffold (
        topBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(TopAppBarDefaults.LargeAppBarCollapsedHeight)
                .background(color = MaterialTheme.colorScheme.secondaryContainer), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavUp) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.str_back))
                }
                CustomSearchTextField(
                    value = text,
                    onValueChange = { text = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.str_search)
                        )
                    },
                    trailingIcon = {
                        if (text.isNotEmpty()) {
                            IconButton(onClick = { text = Constants.EMPTY_STR }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(id = R.string.str_clear_text_icon)
                                )
                            }
                        }
                    }
                )
            }
        }
    ) {
        assetsFilter = assets
        assetsFilter = assets.filter { asset -> asset.assetName.lowercase().contains(text, ignoreCase = true) || asset.assetType.toString().lowercase().contains(text, ignoreCase = true) || asset.modelName.toString().lowercase().contains(text, ignoreCase = true)  || asset.serialNumber.toString().lowercase().contains(text, ignoreCase = true) }
        LazyColumn(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
            items(assetsFilter) { asset ->
                AssetRow(asset) { assetId ->
                    navHostController.navigate("asset_detail/$assetId")
                }
            }
        }
    }
}
