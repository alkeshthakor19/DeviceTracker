package com.devicetracker.ui.dashbord.member

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devicetracker.DataHelper
import com.devicetracker.R
import com.devicetracker.User
import com.devicetracker.ui.AppFloatingButton
import com.devicetracker.ui.Destinations.NEW_MEMBER
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MemberListScreen(openDrawer: () -> Unit, navHostController: NavHostController) {
    val userList = DataHelper.getDummyUserList()
    val context = LocalContext.current
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Member List",  style = MaterialTheme.typography.headlineMedium) },
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
        },
        floatingActionButton = {
            AppFloatingButton {
                Toast.makeText(context,"Floating button clicked", Toast.LENGTH_SHORT).show()
                navHostController.navigate(NEW_MEMBER)
            }
        }
    ) {
        LazyColumn(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            items(userList) {
                UserRow(it) {
                    navHostController.navigate("member_detail/$it")
                }
            }
        }
    }
}

@Composable
fun UserRow(user: User, navigateMemberProfileCallBack: (String)-> Unit) {
    ElevatedCard (
        shape = CutCornerShape(topEnd = 24.dp, bottomStart = 24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ){
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
                .clickable { navigateMemberProfileCallBack.invoke(user.id) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            UserPicture(user)
            UserContent(user = user)
        }
    }
}

@Composable
fun UserPicture(user: User) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_my_profile),
            modifier = Modifier.size(72.dp),
            contentDescription = stringResource(
                id = R.string.app_name
            )
        )
    }
}

@Composable
fun UserContent(user: User) {
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleLarge
        )
        Row {
            Text(text = stringResource(id = R.string.str_emp_id), color = Color.Gray)
            Text(text = user.empCode.toString())
        }
        Row {
            Text(text = stringResource(id = R.string.str_email), color = Color.Gray)
            Text(text = user.email)
        }
    }
}


