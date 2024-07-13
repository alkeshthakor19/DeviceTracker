package com.devicetracker.ui.device

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devicetracker.DataHelper
import com.devicetracker.DeviceType
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DeviceDetailScreen(deviceId: String,onNavUp: () -> Unit) {

    val deviceData = DataHelper.getDeviceById(deviceId.toInt())
    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(titleText = deviceData.name, onNavUp)
        }
    ) {
        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 64.dp)){
            Row(Modifier.padding(top = 4.dp)) {
                Text(text = "Device Type: ")
                Text(text = DeviceType.entries[deviceData.type].name, color = Color.Black)
            }
            Row(Modifier.padding(top = 4.dp)) {
                Text(text = "Model: ")
                Text(text = "Unknown", color = Color.Black)
            }
            Row(Modifier.padding(top = 4.dp)) {
                Text(text = "Current Owner: ")
                Text(text = "Alkesh Thakor", color = Color.Black)
            }
        }
    }
}