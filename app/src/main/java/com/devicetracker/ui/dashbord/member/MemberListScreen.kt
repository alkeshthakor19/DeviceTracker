package com.devicetracker.ui.dashbord.member

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devicetracker.R
import com.devicetracker.core.Constants.INT_SIZE_20
import com.devicetracker.noDoubleClick
import com.devicetracker.ui.AppFloatingButton
import com.devicetracker.ui.Destinations.MEMBER_SEARCH
import com.devicetracker.ui.Destinations.NEW_MEMBER
import com.devicetracker.ui.components.LabelAndTextWithColor
import com.devicetracker.ui.components.MemberEmailRowSection
import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.getFontSizeByPercent
import com.devicetracker.ui.theme.AssetTrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MemberListScreen(openDrawer: () -> Unit, navHostController: NavHostController) {
    val memberViewModel: MemberViewModel = hiltViewModel()
    val isMemberEditablePermission by memberViewModel.isMemberEditablePermission().observeAsState(false)
    val members by memberViewModel.members.observeAsState(initial = emptyList())
    val pullToRefreshState = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        memberViewModel.refreshMembers()
    }
    LaunchedEffect(Unit) {
        memberViewModel.refreshMembers()
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.str_member_list),  style = MaterialTheme.typography.headlineMedium, color = AssetTrackerTheme.colors.textColor) },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = stringResource(id = R.string.str_menu))
                    }
                },
                actions = {
                    IconButton(onClick = { navHostController.navigate(MEMBER_SEARCH) }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = stringResource(id = R.string.str_search))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = Color.Black,
                ),
            )
        },
        floatingActionButton = {
            if(isMemberEditablePermission){
                AppFloatingButton {
                    navHostController.navigate(NEW_MEMBER)
                }
            }
        }
    ) {
        PullToRefreshBox(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
            state = pullToRefreshState,
            isRefreshing = memberViewModel.isLoaderShowing,
            onRefresh = onRefresh,
            contentAlignment = Alignment.TopCenter
        ) {
            if (!memberViewModel.isLoaderShowing) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(members) {
                        UserRow(it) { memberId ->
                            navHostController.navigate("member_detail/$memberId")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserRow(member: Member, navigateMemberProfileCallBack: (String)-> Unit) {
    ElevatedCard (
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        colors = CardDefaults.cardColors(containerColor = AssetTrackerTheme.colors.cardBackgroundColor)
    ){
        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth()
                .noDoubleClick { navigateMemberProfileCallBack.invoke(member.memberId.toString()) },
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
            .padding(6.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = member.memberName,
            style = MaterialTheme.typography.titleLarge
        )
        LabelAndTextWithColor(
            labelText = stringResource(id = R.string.str_emp_code),
            normalText = member.employeeCode.toString(),
            color = Color.Gray,
            fontSize = getFontSizeByPercent(fontSizeInPercent = 4f)
        )
        MemberEmailRowSection(labelText = stringResource(id = R.string.str_email), valueText = member.emailAddress, iconSize = INT_SIZE_20, fontSize = getFontSizeByPercent(fontSizeInPercent = 3.5f))
    }
}



