package com.devicetracker.ui.dashbord.home

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.devicetracker.R
import com.devicetracker.core.Constants.UNASSIGN_ID
import com.devicetracker.noDoubleClick
import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.dashbord.assets.AssetType
import com.devicetracker.ui.getFontSizeByPercent
import com.devicetracker.ui.theme.AssetTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController, openDrawer: () -> Unit) {
    val homeScreenVM: HomeScreenVM = hiltViewModel()

    Column(modifier = Modifier.fillMaxSize()) {
        // TopAppBar has slots for a title, navigation icon,
        // and actions. Also known as the action bar.
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.str_home),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = getFontSizeByPercent(fontSizeInPercent = 5f),
                    color = AssetTrackerTheme.colors.textColor
                )
            },
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
                differentTypeAssetList[assetType.name] = homeScreenVM.fetchAssetsByAssetType(assetType.name).observeAsState().value
            }
            LazyColumn(
                // content padding
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 8.dp,
                    end = 12.dp,
                    bottom = 16.dp
                )
            ){
                /*if (homeScreenVM.isLoaderShowing) {
                    item {
                        ProgressBar()
                    }
                } else {*/
                    items(differentTypeAssetList.entries.toList()){ assetMap ->
                        val assignCount = assetMap.value?.filter { asset -> asset.assetOwnerId != UNASSIGN_ID }?.size?:0
                        val unAssignCount = assetMap.value?.filter { asset -> asset.assetOwnerId == UNASSIGN_ID }?.size?:0
                        AssetShortInfo(assetMap.key, assignCount, unAssignCount) { assetType ->
                            navHostController.navigate("assetsByType/${assetType}")
                        }
                    }
                //}
            }
        }
    }
}

@Composable
fun AssetShortInfo(assetType: String, assignedCount: Int, unassignedCount: Int, navigateAssetListCallBack: (String)-> Unit){
    ElevatedCard (
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(containerColor = AssetTrackerTheme.colors.cardBackgroundColor),
        modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .noDoubleClick {
            navigateAssetListCallBack(assetType)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = assetType,
                fontSize = getFontSizeByPercent(fontSizeInPercent = 5f),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text(
                    text = stringResource(id = R.string.str_assigned_with_count, assignedCount),
                    fontSize = getFontSizeByPercent(fontSizeInPercent = 3.5f)
                )
                Text(
                    text = stringResource(id = R.string.str_unassigned_with_count, unassignedCount),
                    fontSize = getFontSizeByPercent(fontSizeInPercent = 3.5f),
                )
                Text(
                    text = stringResource(R.string.str_total_with_count, (assignedCount + unassignedCount)),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = getFontSizeByPercent(fontSizeInPercent = 4f),
                )
            }
        }
    }
}