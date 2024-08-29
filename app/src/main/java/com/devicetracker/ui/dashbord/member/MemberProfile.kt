package com.devicetracker.ui.dashbord.member

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devicetracker.DataHelper
import com.devicetracker.Device
import com.devicetracker.DeviceType
import com.devicetracker.R
import com.devicetracker.ui.ProgressBar
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.dashbord.assets.DeviceTypePicture


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MemberProfileScreen(memberId: String, onNavUp: () -> Unit) {
    val newMemberViewModel : NewMemberViewModel = hiltViewModel()
    newMemberViewModel.getMemberDetailById(memberId)
    val memberData by newMemberViewModel.member.observeAsState()
    val mTag = "MemberProfileScreen"
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
        if(newMemberViewModel.isLoaderShowing){
            ProgressBar()
        } else {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    ProfilePhoto(memberData?.imageUrl)
                    Row(Modifier.padding(top = 4.dp)) {
                        Text(text = stringResource(id = R.string.str_emp_code))
                        Text(text = "${memberData?.employeeCode ?: "NA"}", color = Color.Black)
                    }
                    Row(Modifier.padding(top = 4.dp)) {
                        Text(text = "Email: ")
                        Text(text = memberData?.emailAddress ?: "NA", color = Color.Black)
                    }
                    Row(Modifier.padding(top = 4.dp)) {
                        Text(text = "Date of Join: ")
                        Text(text = "01-02-2020", color = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        text = "Currently Assigned Assets",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontStyle = FontStyle.Italic
                    )
                }
                AssignAssetListSection()
                item {
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        text = "Previous History",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontStyle = FontStyle.Italic
                    )
                }
                MemberHistorySection()


            }
        }
    }
}

fun LazyListScope.AssignAssetListSection() {
    val deviceList = DataHelper.getAssignAssetDummyList()
    items(deviceList) {
        AssignedAssetRow(device = it) {

        }
    }
}

@Composable
fun AssignedAssetRow(device: Device, navigateDeviceDetailCallBack: (String)-> Unit) {
    ElevatedCard (
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clickable {
                navigateDeviceDetailCallBack.invoke(device.id.toString())
            },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ){
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            DeviceTypePicture(device)
            AssignAssetContent(device)
        }
    }
}

@Composable
fun AssignAssetContent(device: Device) {
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = device.name,
            style = MaterialTheme.typography.titleLarge
        )
        val deviceTypeName = if(device.type == DeviceType.TAB.ordinal) {
            stringResource(id = R.string.str_device_type_tab)
        } else if(device.type == DeviceType.USB.ordinal) {
            stringResource(id = R.string.str_device_type_storage)
        } else if(device.type == DeviceType.CABLE.ordinal) {
            stringResource(id = R.string.str_device_type_cable)
        } else {
            stringResource(id = R.string.str_device_type_other)
        }
        Row {
            Text(text = stringResource(id = R.string.str_asset_type), color = Color.Gray)
            Text(text = deviceTypeName)
        }
        Row {
            Text(text = stringResource(id = R.string.str_asset_assign_date), color = Color.Gray)
            Text(text = "29 Aug 2024")
        }
    }
}

fun LazyListScope.MemberHistorySection() {
    item {
        Text(text = "History 1")
        Text(text = "History 2")
        Text(text = "History 3")
        Text(text = "History 4")
    }
}

@Composable
fun ProfilePhoto(imageUrl : String?){
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.secondary
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
