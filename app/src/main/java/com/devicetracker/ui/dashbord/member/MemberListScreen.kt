package com.devicetracker.ui.dashbord.member

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devicetracker.R
import com.devicetracker.noDoubleClick
import com.devicetracker.ui.AppFloatingButton
import com.devicetracker.ui.Destinations.NEW_MEMBER
import com.devicetracker.ui.ProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MemberListScreen(openDrawer: () -> Unit, navHostController: NavHostController) {
    val memberViewModel: NewMemberViewModel = hiltViewModel()
    //memberViewModel.fetchMembers()
    val members by memberViewModel.members.observeAsState(emptyList())

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
                navHostController.navigate(NEW_MEMBER)
            }
        }
    ) {
        if(memberViewModel.isLoaderShowing){
            ProgressBar()
        } else {
            LazyColumn(modifier = Modifier.padding(top = it.calculateTopPadding())) {
                items(members) {
                    UserRow(it) { memberId ->
                        Log.d("MemberListScreen", "nkp itemClick $memberId")
                        navHostController.navigate("member_detail/$memberId")
                    }
                }
            }
        }
    }
}

@Composable
fun UserRow(member: Member, navigateMemberProfileCallBack: (String)-> Unit) {
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
                .noDoubleClick { navigateMemberProfileCallBack.invoke(member.memberId?:"") },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            UserPicture(member)
            UserContent(member = member)
        }
    }
}

@Composable
fun UserPicture(member: Member) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(member.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_baseline_users),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(72.dp)
        )
    }
}

@Composable
fun UserContent(member: Member) {
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = member.memberName,
            style = MaterialTheme.typography.titleLarge
        )
        Row {
            Text(text = stringResource(id = R.string.str_emp_code), color = Color.Gray)
            Text(text = member.employeeCode.toString())
        }
        Row {
            Text(text = stringResource(id = R.string.str_email), color = Color.Gray)
            Text(text = member.emailAddress)
        }
    }
}


