package com.devicetracker.ui.dashbord.assets

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devicetracker.R
import com.devicetracker.getDateStringFromTimestamp
import com.devicetracker.singleClick
import com.devicetracker.ui.DeleteConfirmationDialog
import com.devicetracker.ui.ProgressBar
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.BlackLabelText
import com.devicetracker.ui.components.LabelAndTextWithColor
import com.devicetracker.ui.components.TextWithLabel
import com.devicetracker.ui.dashbord.member.MemberViewModel
import com.devicetracker.ui.getFontSizeByPercent
import com.devicetracker.ui.getWidthInPercent
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AssetDetailScreen(assetDocId: String, onNavUp: () -> Unit, navHostController: NavHostController) {
    val assetViewModel : AssetViewModel = hiltViewModel()
    val assetData = assetViewModel.asset.observeAsState()
    val assignedHistories by assetViewModel.assetHistories.observeAsState(initial = emptyList())
    val assetEditablePermission by assetViewModel.isAssetEditablePermission().observeAsState(false)
    var isDialogOpen by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val memberViewModel : MemberViewModel = hiltViewModel()
    val assetOwnerDetail by memberViewModel.member.observeAsState()

    LaunchedEffect(assetDocId) {
        assetViewModel.fetchAssetDetailById(assetDocId)
        assetViewModel.getAssetHistories(assetDocId)
    }
    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(
                titleText = assetData.value?.assetName.toString(),
                onNavUp= onNavUp,
                actions = {
                    if(assetEditablePermission) {
                        IconButton(onClick = { isDialogOpen = true }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(
                                id = R.string.str_delete_asset
                            ))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            LaunchedEffect(assetData.value?.assetOwnerId) {
                assetData.value?.assetOwnerId?.let { memberViewModel.fetchMember(it) }
            }
            if( assetEditablePermission || (FirebaseAuth.getInstance().currentUser?.email == assetOwnerDetail?.emailAddress)) {
                FloatingActionButton(
                    onClick = singleClick {
                        navHostController.navigate("edit_asset/${assetDocId}")
                    },
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Asset Detail")
                }
            }
        }
    ) {
        if(assetViewModel.isLoaderShowing){
            ProgressBar()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    AssetDetailSection(assetData.value)
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(20.dp))
                    DescriptionSection(assetData.value?.description)
                    Spacer(modifier = Modifier.height(15.dp))
                    BlackLabelText("Assigned Histories")
                    DeleteConfirmationDialog(
                        title = "Asset Deletion",
                        message = "Are you sure you want to delete this asset?",
                        isDialogOpen = isDialogOpen,
                        onDismiss = { isDialogOpen = false },
                        onConfirm = {
                            assetViewModel.deleteAssetByAssetDocId(assetDocId){
                                Toast.makeText(context, R.string.asset_delete_success_message, Toast.LENGTH_LONG).show()
                                navHostController.popBackStack()
                            }
                        }
                    )
                }
                assetHistorySection(assignedHistories)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailSection(assetData: Asset?){
    val assetOwner = if(assetData != null && !assetData.assetOwnerName.isNullOrEmpty()){
        assetData.assetOwnerName
    } else {
        stringResource(id = R.string.str_un_assign)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = TopAppBarDefaults.TopAppBarExpandedHeight, bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AssetPhoto(assetData?.imageUrl)
        Spacer(modifier = Modifier.height(4.dp))
        assetData?.assetName?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                fontSize = getFontSizeByPercent(fontSizeInPercent = 5f)
            )
        }
        AssetDetailSectionRow(leftText = stringResource(id = R.string.str_asset_id), rightText = assetData?.assetId.toString())
        AssetDetailSectionRow(leftText = stringResource(id = R.string.str_asset_type), rightText = assetData?.assetType.toString())
        AssetDetailSectionRow(leftText = stringResource(id = R.string.str_label_asset_model_name), rightText = assetData?.modelName.toString())
        AssetDetailSectionRow(leftText = stringResource(id = R.string.str_label_asset_serial_number), rightText = assetData?.serialNumber.toString())
        AssetDetailSectionRow(leftText = stringResource(id = R.string.str_asset_quantity), rightText = assetData?.quantity.toString())
        AssetDetailSectionRow(leftText = stringResource(id = R.string.str_project_name), rightText = assetData?.projectName.toString())
        val workingStatus = if(assetData?.assetWorkingStatus == true) {
            stringResource(id = R.string.str_yes)
        } else {
            stringResource(id = R.string.str_no)
        }
        AssetDetailSectionRow(leftText = stringResource(id = R.string.str_working_status), rightText = workingStatus)
        AssetDetailSectionRow(leftText = stringResource(id = R.string.str_current_owner), rightText = assetOwner)
    }
}

@Composable
fun AssetDetailSectionRow(leftText: String, rightText:String){
    Row(horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier.width(getWidthInPercent(45f)), 
            fontSize = getFontSizeByPercent(fontSizeInPercent = 3.4f),
            text = "$leftText:  ", 
            textAlign = TextAlign.End, 
            color = Color.Gray
        )
        Text(
            modifier = Modifier.width(getWidthInPercent(55f)), 
            fontSize = getFontSizeByPercent(fontSizeInPercent = 3.4f),
            text = rightText, 
            textAlign = TextAlign.Start
        )
    }
}


@Composable
fun DescriptionSection(text: String?) {
    if (!text.isNullOrBlank()) {
        TextWithLabel(stringResource(id = R.string.str_description), text)
    }
}

@Composable
fun AssignHistoryRow(assetHistory: AssetHistory, navigateDeviceDetailCallBack: (String)-> Unit) {
    Card (
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clickable {
                assetHistory.id?.let { navigateDeviceDetailCallBack.invoke(it) }
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
            AssignHistoryContent(assetHistory)
        }
    }
}

@Composable
fun AssignHistoryContent(assetHistory: AssetHistory) {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = assetHistory.assetOwnerName.toString(),
            style = MaterialTheme.typography.titleMedium
        )
        LabelAndTextWithColor(labelText = stringResource(id = R.string.str_assigned_by), normalText = assetHistory.adminEmail.toString(), color = Color.Gray)
        LabelAndTextWithColor(labelText = stringResource(id = R.string.str_asset_assign_date), normalText = getDateStringFromTimestamp(assetHistory.createdAt), color = Color.Gray)
    }
}

fun LazyListScope.assetHistorySection(assignedHistories: List<AssetHistory>) {
    items(assignedHistories.size) { index ->
        AssignHistoryRow(assetHistory = assignedHistories[index]) {
            // TODO for click event
        }
    }
}

@Composable
fun AssetPhoto(imageUrl : String?){
    Card(
        shape = RoundedCornerShape(5.dp),
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
            placeholder = painterResource(R.drawable.ic_devices),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(130.dp)
        )
    }
}
