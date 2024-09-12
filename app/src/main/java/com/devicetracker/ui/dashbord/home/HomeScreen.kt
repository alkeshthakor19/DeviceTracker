package com.devicetracker.ui.dashbord.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devicetracker.DataHelper
import com.devicetracker.core.Constants.UNASSIGN_ID
import com.devicetracker.ui.ProgressBar
import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.dashbord.assets.AssetType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(openDrawer: () -> Unit) {
    val homeScreenVM: HomeScreenVM = hiltViewModel()

    @Composable
    fun getAssetByType(assetType:String): List<Asset> {
        val assetList by homeScreenVM.fetchAssetsByAssetType(assetType).observeAsState(emptyList())
        return  assetList
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TopAppBar has slots for a title, navigation icon,
        // and actions. Also known as the action bar.
        TopAppBar(
            title = { Text("Home", style = MaterialTheme.typography.headlineMedium) },
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
        Surface(modifier = Modifier.weight(1f)) {
            val differentTypeAssetList = mutableMapOf<String, List<Asset>?>()
            AssetType.entries.forEach { assetType ->
                differentTypeAssetList[assetType.name] = getAssetByType(assetType.name)
            }
            LazyColumn(
                // content padding
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 16.dp,
                    end = 12.dp,
                    bottom = 16.dp
                )
            ){
                if (homeScreenVM.isLoaderShowing) {
                    item {
                        ProgressBar()
                    }
                } else {
                    items(differentTypeAssetList.entries.toList()){
                        val assignCount = it.value?.filter { it.assetOwnerId != UNASSIGN_ID }?.size?:0
                        val unAssignCount = it.value?.filter { it.assetOwnerId == UNASSIGN_ID }?.size?:0
                        AssetShortInfo(it.key, assignCount, unAssignCount)
                    }
                }
            }
        }
    }
}

@Composable
fun AssetShortInfo(assetType: String, assignedAsset: Int, unAssignedAsset: Int){
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total number of $assetType : ",
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = (assignedAsset + unAssignedAsset).toString(),
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Assigned $assetType : " ,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(text = assignedAsset.toString(),
                    fontSize = 18.sp,
                    color = Color.Black,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Un Assigned $assetType : " ,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(text = unAssignedAsset.toString(),
                    fontSize = 18.sp,
                    color = Color.Black,
                )
            }
        }
    }
}

/*
@Preview
@Composable
private fun HomeScreenPreview() {
    DeviceTrackerTheme {
        HomeScreen()
    }
}*/
