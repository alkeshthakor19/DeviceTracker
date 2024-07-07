package com.devicetracker.ui.member

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.devicetracker.DataHelper
import com.devicetracker.R
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation


@Composable
fun MemberProfileScreen(memberId: String,onNavUp: () -> Unit) {
    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(titleText = "Member", onNavUp)
        }
    ) {
        val memberData = DataHelper.getMemberById(memberId)

        Text(text = memberData.name, modifier = Modifier.padding(it))
    }
}
