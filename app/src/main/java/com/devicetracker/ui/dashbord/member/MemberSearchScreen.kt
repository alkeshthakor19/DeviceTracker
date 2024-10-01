package com.devicetracker.ui.dashbord.member

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.devicetracker.core.Constants
import com.devicetracker.ui.components.CustomSearchTextField
import com.devicetracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberSearchScreen(navHostController: NavHostController, onNavUp: () -> Unit){
    val memberViewModel: MemberViewModel = hiltViewModel()
    var text by rememberSaveable { mutableStateOf(Constants.EMPTY_STR) }
    val members by memberViewModel.members.observeAsState(emptyList())
    var membersFilter: List<Member>

    LaunchedEffect(Unit) {
        memberViewModel.refreshMembers()
    }
    Scaffold (
        topBar = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(TopAppBarDefaults.LargeAppBarCollapsedHeight)
                .background(color = MaterialTheme.colorScheme.secondaryContainer), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavUp) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.str_back))
                }
                CustomSearchTextField(
                    value = text,
                    onValueChange = { text = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    trailingIcon = {
                        if (text.isNotEmpty()) {
                            IconButton(onClick = { text = Constants.EMPTY_STR }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear Text Icon"
                                )
                            }
                        }
                    }
                )
            }
        }
    ) {
        membersFilter = members
        membersFilter = members.filter { member -> member.memberName.lowercase().contains(text, ignoreCase = true) || member.employeeCode.toString().lowercase().contains(text, ignoreCase = true) || member.emailAddress.lowercase().contains(text, ignoreCase = true)}
        LazyColumn(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
            items(membersFilter) { member ->
                UserRow(member) { memberId ->
                    navHostController.navigate("member_detail/$memberId")
                }
            }
        }
    }
}
