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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.devicetracker.core.Constants
import com.devicetracker.ui.components.CustomSearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetSearchScreen(navHostController: NavHostController, onNavUp: () -> Unit){
    val assetViewModel: AssetViewModel = hiltViewModel()
    var text by rememberSaveable { mutableStateOf(Constants.EMPTY_STR) }
    var assets = emptyList<Asset>()
    var assetsFilter: List<Asset>
    assetViewModel.assets.observe(LocalLifecycleOwner.current) {
        assets = it
    }
    Scaffold (
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().height(TopAppBarDefaults.LargeAppBarCollapsedHeight).background(color = MaterialTheme.colorScheme.secondaryContainer), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavUp) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                CustomSearchTextField(
                    value = text,
                    onValueChange = { text = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    trailingIcon = {
                        if (text.isNotEmpty()) {
                            IconButton(onClick = { text = Constants.EMPTY_STR }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear Text Icon"
                                )
                            }
                        }
                    }
                )
            }
        }
    ) {
        assetsFilter = assets
        assetsFilter = assets.filter { it.assetName.lowercase().contains(text, ignoreCase = true) || it.assetType.toString().lowercase().contains(text, ignoreCase = true) || it.modelName.toString().lowercase().contains(text, ignoreCase = true)  || it.serialNumber.toString().lowercase().contains(text, ignoreCase = true) }
        LazyColumn(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
            items(assetsFilter) {
                AssetRow(it) { assetId ->
                    navHostController.navigate("asset_detail/$assetId")
                }
            }
        }
    }
}
