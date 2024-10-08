package com.devicetracker.ui.dashbord.member

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devicetracker.R
import com.devicetracker.core.Constants.INT_SIZE_24
import com.devicetracker.core.Constants.INT_SIZE_80
import com.devicetracker.getDateStringFromTimestamp
import com.devicetracker.singleClick
import com.devicetracker.ui.DeleteConfirmationDialog
import com.devicetracker.ui.ProgressBar
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.LabelAndTextWithColor
import com.devicetracker.ui.components.MemberEmailRowSection
import com.devicetracker.ui.components.NoDataMessage
import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.dashbord.assets.AssetPicture
import com.devicetracker.ui.dashbord.assets.AssetViewModel
import com.devicetracker.ui.getFontSizeByPercent
import com.devicetracker.ui.getWidthInPercent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MemberProfileScreen(
    memberId: String,
    onNavUp: () -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val memberViewModel : MemberViewModel = hiltViewModel()
    val memberData by memberViewModel.member.observeAsState()

    val assetViewModel: AssetViewModel = hiltViewModel()
    val assetListByMemberId = remember { mutableStateOf<List<Asset>>(emptyList()) }
    assetViewModel.getAssetListByMemberId(memberId).observe(LocalLifecycleOwner.current) {
        assetListByMemberId.value = it
    }
    val coroutineScope = rememberCoroutineScope()
    val onRefreshAssetList: () -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            assetListByMemberId.value = assetViewModel.fetchAssetListByMemberId(memberId)
        }
    }

    val memberEditablePermission by memberViewModel.isMemberEditablePermission().observeAsState(false)
    var isDialogOpen by remember { mutableStateOf(false) }
    LaunchedEffect(memberId) {
        memberViewModel.fetchMember(memberId)
    }
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(
                titleText = memberData?.memberName.toString(),
                onNavUp = onNavUp,
                actions = {
                    if(memberEditablePermission) {
                        IconButton(onClick = { isDialogOpen = true }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(
                                id = R.string.str_delete_member
                            ))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if(memberEditablePermission || memberData?.emailAddress == currentUserEmail) {
                FloatingActionButton(onClick = singleClick {
                    navHostController.navigate("edit_member/${memberId}")
                }, modifier = Modifier.padding(bottom = 24.dp)) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(id = R.string.str_edit_member))
                }
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
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = stringResource(id = R.string.str_currently_assigned_assets),
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.width(50.dp))
                        Text(text = stringResource(id = R.string.str_refresh_list), modifier = Modifier
                            .padding(bottom = 2.dp)
                            .clickable { onRefreshAssetList() } )
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
                        assignAssetListSection(assetListByMemberId.value, navHostController)
                    }
                }
            }
            DeleteConfirmationDialog(
                title = stringResource(id = R.string.member_delete_title),
                message = stringResource(id = R.string.member_delete_message),
                isDialogOpen = isDialogOpen,
                onDismiss = { isDialogOpen = false },
                onConfirm = {
                    memberViewModel.deleteMemberByMemberDocId(memberId){
                        Toast.makeText(context, R.string.member_delete_success_message, Toast.LENGTH_LONG).show()
                        navHostController.popBackStack()
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileDetailSection(memberData: Member?){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 78.dp, bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        ProfilePhoto(memberData?.imageUrl)
        Spacer(modifier = Modifier.height(4.dp))
        if(memberData?.memberName != null) {
            Text(
                text = memberData.memberName,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                fontSize = getFontSizeByPercent(fontSizeInPercent = 5f)
            )
        }
        MemberFieldRow(labelText = stringResource(id = R.string.str_emp_code), bodyText = memberData?.employeeCode.toString())
        if(!memberData?.mobileNumber.isNullOrEmpty()) {
            MemberFieldRow(
                labelText = stringResource(id = R.string.str_mobile_number),
                bodyText = memberData?.mobileNumber.toString()
            )
        }
        MemberEmailRowSection(
            labelText = stringResource(id = R.string.str_email),
            valueText = memberData?.emailAddress.toString(),
            iconSize = INT_SIZE_24,
            fontSize = getFontSizeByPercent(fontSizeInPercent = 4f)
        )
    }
}

@Composable
fun MemberFieldRow(labelText: String, bodyText: String){
    Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.Center) {
        Text(modifier = Modifier.width(getWidthInPercent(50f)), text = "$labelText: ", textAlign = TextAlign.End, fontSize = getFontSizeByPercent(fontSizeInPercent = 4f), color = Color.Gray)
        Text(modifier = Modifier.width(getWidthInPercent(50f)), text = bodyText, textAlign = TextAlign.Start, fontSize = getFontSizeByPercent(fontSizeInPercent = 4f))
    }
}

fun LazyListScope.assignAssetListSection(
    assetListByMemberId: List<Asset>,
    navHostController: NavHostController
) {
    items(assetListByMemberId) {
        AssignedAssetRow(asset = it) { assetId ->
            navHostController.navigate("asset_detail/${assetId}")
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
                navigateDeviceDetailCallBack.invoke(asset.assetDocId.toString())
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
            AssetPicture(asset, INT_SIZE_80)
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
        LabelAndTextWithColor(labelText = stringResource(id = R.string.str_asset_type), normalText = asset.assetType.toString(), color = Color.Gray)
        LabelAndTextWithColor(labelText = stringResource(id = R.string.str_label_asset_model_name), normalText = asset.modelName.toString(), color = Color.Gray)
        LabelAndTextWithColor(labelText = stringResource(id = R.string.str_asset_assign_date), normalText = getDateStringFromTimestamp(asset.createdAt), color = Color.Gray, fontSize = getFontSizeByPercent(3.5f))
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
            error = painterResource(id = R.drawable.ic_baseline_users),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(120.dp)
        )
    }
}
