package com.devicetracker.ui.dashbord.member

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devicetracker.R
import com.devicetracker.getDateStringFromTimestamp
import com.devicetracker.ui.ProgressBar
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.BodyText
import com.devicetracker.ui.components.LabelText
import com.devicetracker.ui.components.NoDataMessage
import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.dashbord.assets.AssetType
import com.devicetracker.ui.dashbord.assets.AssetPicture
import com.devicetracker.ui.dashbord.assets.AssetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MemberProfileScreen(memberId: String, onNavUp: () -> Unit) {
    val mTag = "MemberProfileScreen"
    val memberViewModel : MemberViewModel = hiltViewModel()
    val memberData by memberViewModel.fetchMember(memberId).observeAsState()

    val assetViewModel: AssetViewModel = hiltViewModel()
    val assetListByMemberId = remember { mutableStateOf<List<Asset>>(emptyList()) }
    assetViewModel.getAssetListByMemberId(memberId).observe(LocalLifecycleOwner.current) {
        assetListByMemberId.value = it
    }
    val coroutineScope = rememberCoroutineScope()
    val onRefreshAssetList: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            assetListByMemberId.value = assetViewModel.fetchAssetListByMemberId(memberId)
            Log.d("MemberProfileScreen", "nkp assetListByMemberId1 size ${assetListByMemberId.value.size}")
        }
    }
    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(titleText = memberData?.memberName?: "NA", onNavUp)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { }, modifier = Modifier.padding(bottom = 24.dp) ) {
                Icon(Icons.Filled.Edit, contentDescription ="Edit Member Detail" )
            }
        }
    ) {
        if(memberViewModel.isLoaderShowing){
            ProgressBar()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    ProfileDetailSection(memberData)
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "Currently Assigned Assets",
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.width(50.dp))
                        Text(text = "Refresh List", modifier = Modifier.padding(bottom = 2.dp).clickable { onRefreshAssetList() } )
                    }
                }
                if(assetViewModel.isLoaderShowing){
                    item { ProgressBar() }
                } else {
                    if(assetListByMemberId.value.isEmpty()){
                        item {
                            NoDataMessage()
                        }
                    } else {
                        assignAssetListSection(assetListByMemberId.value)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailSection(memberData: Member?){
    ElevatedCard (
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        //CutCornerShape(topStart = 18.dp, bottomEnd = 18.dp),
        modifier = Modifier
            .padding(top = 60.dp, bottom = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            ProfilePhoto(memberData?.imageUrl)
            if(memberData?.memberName != null) {
                Text(
                    text = memberData.memberName,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    fontSize = 24.sp
                )
            }
            MemberFieldRow(labelText = stringResource(id = R.string.str_emp_code), bodyText = memberData?.employeeCode.toString())
            MemberFieldRow(labelText = stringResource(id = R.string.str_email), bodyText = memberData?.emailAddress ?: "NA")
        }
    }
}

@Composable
fun MemberFieldRow(labelText: String, bodyText: String){
    Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        LabelText(labelText = labelText)
        BodyText(bodyText = bodyText)
    }
}

fun LazyListScope.assignAssetListSection(assetListByMemberId: List<Asset>) {
    Log.d("MemberProfileScreen", "nkp list assetListByMemberId size ${assetListByMemberId.size}")
    items(assetListByMemberId) {
        AssignedAssetRow(asset = it) {

        }
    }
}

@Composable
fun AssignedAssetRow(asset: Asset, navigateDeviceDetailCallBack: (String)-> Unit) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clickable {
                navigateDeviceDetailCallBack.invoke(asset.assetId.toString())
            },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primaryContainer)

    ){
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 5.dp, bottom = 5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AssetPicture(asset)
            AssignAssetContent(asset)
        }
    }
}

@Composable
fun AssignAssetContent(asset: Asset) {
    Column(
        Modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = asset.assetName,
            style = MaterialTheme.typography.titleMedium,
        )
        Row {
            Text(text = stringResource(id = R.string.str_asset_type), color = Color.Gray)
            Text(text = asset.assetType?:"")
        }
        Row {
            Text(text = stringResource(id = R.string.str_label_asset_model_name), color = Color.Gray)
            Text(text = asset.modelName?:"")
        }
        Row {
            Text(text = stringResource(id = R.string.str_asset_assign_date), color = Color.Gray)
            Text(text = getDateStringFromTimestamp(asset.createdAt))
        }
    }
}

@Composable
fun ProfilePhoto(imageUrl : String?){
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.secondary,
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Gray),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_baseline_users),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(120.dp)
        )
    }
}
