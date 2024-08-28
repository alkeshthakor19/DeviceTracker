package com.devicetracker.ui.dashbord.member

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devicetracker.DataHelper
import com.devicetracker.DataHelper.getDummyUserList
import com.devicetracker.R
import com.devicetracker.User
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MemberProfileScreen(memberData: Member,onNavUp: () -> Unit) {
    val mTag = "MemberProfileScreen"
    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(titleText = memberData.memberName, onNavUp)
        }
    ) {
        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 64.dp)){
            Row(Modifier.padding(top = 4.dp)) {
                Text(text = stringResource(id = R.string.str_emp_code))
                Text(text = "${memberData.employeeCode}", color = Color.Black)
            }
            Row(Modifier.padding(top = 4.dp)) {
                Text(text = "Email: ")
                Text(text = memberData.emailAddress, color = Color.Black)
            }
            Row(Modifier.padding(top = 4.dp)) {
                Text(text = "Date of Join: ")
                Text(text = "01-02-2020", color = Color.Black)
            }
        }
    }
}
