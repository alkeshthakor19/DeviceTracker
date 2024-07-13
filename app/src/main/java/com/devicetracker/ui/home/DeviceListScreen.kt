package com.devicetracker.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicetracker.DataHelper
import com.devicetracker.Device
import com.devicetracker.DeviceType
import com.devicetracker.R
import com.devicetracker.ui.AppFloatingButton

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DeviceListScreen(navigateDeviceDetailCallBack: (String)-> Unit) {
    val deviceList = DataHelper.getDeviceDummyList()
    Scaffold(floatingActionButton = {
        AppFloatingButton {

        }
    }) {
        LazyColumn {
            items(deviceList) {
                DeviceRow(device = it, navigateDeviceDetailCallBack)
            }
        }
    }
}

@Composable
fun DeviceRow(device: Device, navigateDeviceDetailCallBack: (String)-> Unit) {
    ElevatedCard (
        shape = CutCornerShape(topEnd = 24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
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
            DeviceContent(device)
        }
    }
}

@Composable
fun DeviceTypePicture(device: Device) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    ) {
        val resourceId = if(device.type == DeviceType.TAB.ordinal) {
            R.drawable.ic_devices
        } else if(device.type == DeviceType.CABLE.ordinal) {
            R.drawable.ic_baseline_cable
        } else {
            R.drawable.ic_devices_other
        }
        Image(
            painter = painterResource(id = resourceId),
            modifier = Modifier.size(76.dp),
            contentDescription = stringResource(
            id = R.string.app_name
        ),
            contentScale = ContentScale.Inside)
    }
}

@Composable
fun DeviceContent(device: Device) {
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
              Text(text = stringResource(id = R.string.str_device_type), color = Color.Gray)
              Text(text = deviceTypeName)
          }
    }
}
