package com.devicetracker.ui.dashbord.member

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.devicetracker.core.Constants
import com.devicetracker.ui.AppFloatingButton
import com.devicetracker.ui.Destinations.NEW_MEMBER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberSearchScreen(navHostController: NavHostController, onNavUp: () -> Unit){
    val memberViewModel: MemberViewModel = hiltViewModel()
    var text by rememberSaveable { mutableStateOf(Constants.EMPTY_STR) }
    var members = emptyList<Member>()
    var membersFilter: List<Member>
    memberViewModel.members.observe(LocalLifecycleOwner.current) {
        members = it
    }
    Scaffold (
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().height(TopAppBarDefaults.LargeAppBarCollapsedHeight).background(color = MaterialTheme.colorScheme.secondaryContainer), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavUp) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
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
        membersFilter = members.filter { it.memberName.lowercase().contains(text, ignoreCase = true) || it.employeeCode.toString().lowercase().contains(text, ignoreCase = true) || it.emailAddress.lowercase().contains(text, ignoreCase = true)}
        LazyColumn(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
            items(membersFilter) {
                UserRow(it) { memberId ->
                    navHostController.navigate("member_detail/$memberId")
                }
            }
        }
    }
}

@Composable
fun CustomSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: String = "Search..."
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder, color = Color.Gray)
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    )
}