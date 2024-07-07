package com.devicetracker.ui.device

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.devicetracker.DataHelper
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation

@Composable
fun DeviceDetailScreen(deviceId: String,onNavUp: () -> Unit) {
    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(titleText = "Device Details", onNavUp)
        }
    ) {
        val deviceData = DataHelper.getDeviceById(deviceId.toInt())

        Text(text = deviceData.name, modifier = Modifier.padding(it))
    }
}